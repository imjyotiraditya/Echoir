package dev.jyotiraditya.echoir.data.repository

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.jyotiraditya.echoir.data.local.dao.DownloadDao
import dev.jyotiraditya.echoir.data.media.FFmpegProcessor
import dev.jyotiraditya.echoir.data.media.MetadataManager
import dev.jyotiraditya.echoir.data.remote.api.ApiService
import dev.jyotiraditya.echoir.data.remote.mapper.PlaybackMapper.toDomain
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.domain.model.PlaybackRequest
import dev.jyotiraditya.echoir.domain.model.PlaybackResponse
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import dev.jyotiraditya.echoir.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val downloadDao: DownloadDao,
    private val settingsRepository: SettingsRepository,
    private val ffmpegProcessor: FFmpegProcessor,
    private val metadataManager: MetadataManager,
    @ApplicationContext private val context: Context
) : DownloadRepository {

    companion object {
        private const val TAG = "DownloadRepository"
    }

    override suspend fun createAlbumDirectory(albumTitle: String, explicit: Boolean): String {
        return withContext(Dispatchers.IO) {
            when (val customDir = settingsRepository.getOutputDirectory()) {
                null -> {
                    val musicDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    val echoirDir = File(musicDir, "Echoir").apply { mkdirs() }
                    val albumDirName = if (explicit) "$albumTitle (E)" else albumTitle
                    val safeDirName = getNextAvailableName(sanitizeFileName(albumDirName)) {
                        File(echoirDir, it).exists()
                    }
                    File(echoirDir, safeDirName).apply { mkdirs() }.absolutePath
                }

                else -> {
                    val uri = Uri.parse(customDir)
                    val directory = DocumentFile.fromTreeUri(context, uri)
                        ?: throw IOException("Could not access directory")

                    val albumDirName = if (explicit) "$albumTitle (E)" else albumTitle
                    val safeDirName = getNextAvailableName(sanitizeFileName(albumDirName)) {
                        directory.findFile(it) != null
                    }
                    val albumDir = directory.createDirectory(safeDirName)
                        ?: throw IOException("Could not create album directory")

                    albumDir.uri.toString()
                }
            }
        }
    }

    override suspend fun processDownload(
        downloadId: String,
        trackId: Long,
        quality: String,
        ac4: Boolean,
        immersive: Boolean,
        onProgress: suspend (Int) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            Log.d(TAG, "Starting download for track: $trackId with quality: $quality")

            val (playbackInfo, metadata) = getDownloadInfo(
                PlaybackRequest(
                    id = trackId,
                    quality = quality,
                    ac4 = ac4,
                    immersive = immersive
                )
            )

            updateDownloadStatus(downloadId, DownloadStatus.DOWNLOADING)

            // Get cache file path for final processing
            val extension = if (playbackInfo.codec == "flac") "flac" else "m4a"
            val cacheFile = File(context.cacheDir, "${trackId}_cache.$extension")

            // Handle single vs multiple parts
            if (playbackInfo.urls.size == 1) {
                downloadSingleFile(playbackInfo.urls.first(), cacheFile)
                onProgress(100)
            } else {
                downloadMultipleFiles(playbackInfo.urls, cacheFile, onProgress)
            }

            // Process with FFmpeg if needed
            if (playbackInfo.urls.size > 1) {
                updateDownloadStatus(downloadId, DownloadStatus.MERGING)
                processDownloadedFile(cacheFile, extension)
            }

            // Embed metadata while file is still in cache
            metadataManager.embedMetadata(cacheFile.absolutePath, metadata)

            // Get download info and generate filename
            val download =
                getDownloadById(downloadId) ?: throw IOException("Download info not found")

            // Generate safe filename
            val safeFileName = generateFileName(download)

            // Move to final location
            val finalPath = when (val customDir = settingsRepository.getOutputDirectory()) {
                null -> {
                    val musicDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    val echoirDir = File(musicDir, "Echoir").apply { mkdirs() }

                    val targetDir = when {
                        download.albumDirectory != null -> {
                            File(download.albumDirectory)
                        }

                        else -> echoirDir
                    }

                    val finalFileName = getNextAvailableName(safeFileName) { name ->
                        File(targetDir, "$name.$extension").exists()
                    }
                    val finalFile = File(targetDir, "$finalFileName.$extension")

                    cacheFile.copyTo(finalFile, overwrite = false)
                    scanMedia(finalFile.absolutePath)
                    finalFile.absolutePath
                }

                else -> {
                    val uri = Uri.parse(customDir)
                    val directory = DocumentFile.fromTreeUri(context, uri)
                        ?: throw IOException("Could not access directory")

                    val targetDir = when {
                        download.albumDirectory != null -> {
                            DocumentFile.fromTreeUri(context, Uri.parse(download.albumDirectory))
                                ?: throw IOException("Could not access album directory")
                        }

                        else -> directory
                    }

                    val finalFileName = getNextAvailableName(safeFileName) { name ->
                        targetDir.findFile("$name.$extension") != null
                    }

                    val finalFile = targetDir.createFile("*/*", "$finalFileName.$extension")
                        ?: throw IOException("Could not create file")

                    context.contentResolver.openOutputStream(finalFile.uri)?.use { output ->
                        cacheFile.inputStream().use { it.copyTo(output) }
                    } ?: throw IOException("Could not open output stream")

                    finalFile.uri.toString()
                }
            }

            // Clean up cache
            cacheFile.delete()

            // Update status
            updateDownloadStatus(downloadId, DownloadStatus.COMPLETED)
            updateDownloadProgress(downloadId, 100)
            updateDownloadFilePath(downloadId, finalPath)

            finalPath
        }.onFailure { error ->
            val errorMessage = error.message ?: "Unknown error occurred"
            val errorDetails = error.stackTraceToString()

            Log.e(
                TAG, """
                Download failed:
                Track ID: $trackId
                Quality: $quality
                Device: ${Build.MANUFACTURER} ${Build.MODEL}
                Android: ${Build.VERSION.SDK_INT}
                Error: $errorMessage
                Stack trace:
                $errorDetails
            """.trimIndent()
            )

            downloadDao.update(
                downloadDao.getDownloadById(downloadId)?.copy(
                    status = DownloadStatus.FAILED,
                    errorMessage = errorMessage,
                    errorDetails = errorDetails
                ) ?: throw error
            )
        }
    }

    private suspend fun downloadSingleFile(url: String, outputFile: File) {
        outputFile.outputStream().use {
            apiService.downloadFileToStream(url, it)
        }
    }

    private suspend fun downloadMultipleFiles(
        urls: List<String>,
        outputFile: File,
        onProgress: suspend (Int) -> Unit
    ) {
        val tempFiles = urls.mapIndexed { index, url ->
            val tempFile =
                File(context.cacheDir, "${outputFile.nameWithoutExtension}_part_$index.tmp")
            val response = apiService.downloadFile(url)
            tempFile.writeBytes(response)
            onProgress((index + 1) * 100 / urls.size)
            tempFile
        }

        outputFile.outputStream().use { output ->
            tempFiles.forEach { input ->
                input.inputStream().use { it.copyTo(output) }
                input.delete()
            }
        }
    }

    private suspend fun processDownloadedFile(file: File, extension: String) {
        val processedFile =
            File(context.cacheDir, "${file.nameWithoutExtension}_processed.$extension")
        ffmpegProcessor.processMergedFile(file.absolutePath, processedFile.absolutePath)
        processedFile.copyTo(file, overwrite = true)
        processedFile.delete()
    }

    private fun sanitizeFileName(title: String): String {
        return title.replace("""[\\/:*?"<>|]""".toRegex(), "")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    private suspend fun generateFileName(download: Download): String {
        val format = settingsRepository.getFileNamingFormat()
        val fileName = format.format(download.artist, download.title)
        return sanitizeFileName(fileName)
    }

    private fun getNextAvailableName(baseName: String, exists: (name: String) -> Boolean): String {
        var index = 0
        var name = baseName
        while (exists(name)) {
            index++
            name = "$baseName ($index)"
        }
        return name
    }

    private suspend fun scanMedia(path: String) {
        withContext(Dispatchers.Main) {
            MediaScannerConnection.scanFile(
                context,
                arrayOf(path),
                arrayOf("audio/*")
            ) { scannedPath, uri ->
                if (uri != null) {
                    Log.i(TAG, "Media scan completed for: $scannedPath")
                    Log.i(TAG, "Scanned URI: $uri")
                } else {
                    Log.e(TAG, "Media scan failed for: $scannedPath")
                }
            }
        }
    }

    override suspend fun getDownloadInfo(request: PlaybackRequest): Pair<PlaybackResponse, Map<String, String>> =
        apiService.getDownloadInfo(request)
            .let { (playbackDto, metadata) ->
                playbackDto.toDomain() to metadata
            }

    override suspend fun saveDownload(download: Download) =
        downloadDao.insert(download)

    override suspend fun updateDownloadProgress(downloadId: String, progress: Int) =
        downloadDao.updateProgress(downloadId, progress)

    override suspend fun updateDownloadStatus(downloadId: String, status: DownloadStatus) =
        downloadDao.updateStatus(downloadId, status)

    override suspend fun updateDownloadFilePath(downloadId: String, filePath: String) =
        downloadDao.updateFilePath(downloadId, filePath)

    override suspend fun deleteDownload(download: Download) =
        downloadDao.delete(download)

    override suspend fun getDownloadById(downloadId: String): Download? =
        downloadDao.getDownloadById(downloadId)

    override suspend fun getDownloadsByTrackId(trackId: Long): List<Download> =
        downloadDao.getDownloadsByTrackId(trackId)

    override suspend fun getDownloadsByAlbumId(albumId: Long): List<Download> =
        downloadDao.getDownloadsByAlbumId(albumId)

    override fun getActiveDownloads(): Flow<List<Download>> =
        downloadDao.getDownloadsByStatus(
            listOf(
                DownloadStatus.QUEUED,
                DownloadStatus.DOWNLOADING,
                DownloadStatus.MERGING
            )
        )

    override fun getDownloadHistory(): Flow<List<Download>> =
        downloadDao.getDownloadsByStatus(
            listOf(
                DownloadStatus.COMPLETED,
                DownloadStatus.FAILED
            )
        )
}