package dev.jyotiraditya.echoir.data.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.jyotiraditya.echoir.data.local.dao.DownloadDao
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
    @ApplicationContext private val context: Context
) : DownloadRepository {

    override suspend fun getPlaybackInfo(request: PlaybackRequest): PlaybackResponse =
        apiService.getPlaybackInfo(request).toDomain()

    override suspend fun downloadFile(url: String, fileName: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val customDir = settingsRepository.getOutputDirectory()

                if (customDir != null) {
                    // Use custom directory
                    val uri = Uri.parse(customDir)
                    val directory = DocumentFile.fromTreeUri(context, uri)
                        ?: throw IOException("Could not access directory")

                    val file = directory.createFile("*/*", fileName)
                        ?: throw IOException("Could not create file")

                    context.contentResolver.openOutputStream(file.uri)?.use { outputStream ->
                        val response = apiService.downloadFile(url)
                        outputStream.write(response)
                    } ?: throw IOException("Could not open output stream")

                    file.uri.toString()
                } else {
                    // Use default directory
                    val file = File(context.getExternalFilesDir(null), fileName)
                    val response = apiService.downloadFile(url)
                    file.writeBytes(response)
                    file.absolutePath
                }
            }
        }

    override suspend fun mergeFiles(
        files: List<String>,
        outputFile: String,
        codec: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val customDir = settingsRepository.getOutputDirectory()

            if (customDir != null) {
                // Handle merge in custom directory
                val uri = Uri.parse(customDir)
                val directory = DocumentFile.fromTreeUri(context, uri)
                    ?: throw IOException("Could not access directory")

                val output = directory.createFile("*/*", outputFile)
                    ?: throw IOException("Could not create output file")

                context.contentResolver.openOutputStream(output.uri)?.use { outputStream ->
                    files.forEach { inputPath ->
                        val inputUri = Uri.parse(inputPath)
                        context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                } ?: throw IOException("Could not open output stream")

                // Clean up source files
                files.forEach { inputPath ->
                    val inputUri = Uri.parse(inputPath)
                    DocumentFile.fromSingleUri(context, inputUri)?.delete()
                }

                output.uri.toString()
            } else {
                // Handle merge in default directory
                val output = File(context.getExternalFilesDir(null), outputFile)
                output.outputStream().use { outputStream ->
                    files.forEach { input ->
                        File(input).inputStream().use { it.copyTo(outputStream) }
                    }
                }

                // Clean up source files
                files.forEach { File(it).delete() }

                output.absolutePath
            }
        }
    }

    override suspend fun saveDownload(download: Download) =
        downloadDao.insert(download)

    override suspend fun updateDownloadProgress(id: Long, progress: Int) =
        downloadDao.updateProgress(id, progress)

    override suspend fun updateDownloadStatus(id: Long, status: DownloadStatus) =
        downloadDao.updateStatus(id, status)

    override suspend fun updateDownloadFilePath(id: Long, filePath: String) =
        downloadDao.updateFilePath(id, filePath)

    override suspend fun deleteDownload(download: Download) =
        downloadDao.delete(download)

    override suspend fun getDownloadById(id: Long): Download? =
        downloadDao.getDownloadById(id)

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