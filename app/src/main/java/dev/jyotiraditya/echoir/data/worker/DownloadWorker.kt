package dev.jyotiraditya.echoir.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.domain.model.PlaybackRequest
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import dev.jyotiraditya.echoir.domain.usecase.DownloadTrackUseCase
import java.io.File

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadRepository: DownloadRepository,
    private val downloadTrackUseCase: DownloadTrackUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val id = inputData.getLong("id", 0)
        val download = downloadRepository.getDownloadById(id)
        val title = download?.title ?: "Unknown Track"
        val isMerging = download?.status == DownloadStatus.MERGING

        return downloadTrackUseCase.createForegroundInfo(
            id = id,
            title = title,
            progress = download?.progress ?: 0,
            indeterminate = isMerging
        )
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong("id", 0)
        val quality = inputData.getString("quality") ?: return Result.failure()
        val ac4 = inputData.getBoolean("ac4", false)
        val immersive = inputData.getBoolean("immersive", false)

        return try {
            Log.d("DownloadWorker", "Starting download for id: $id, quality: $quality")

            val playbackInfo = downloadRepository.getPlaybackInfo(
                PlaybackRequest(
                    id,
                    quality,
                    ac4 = ac4,
                    immersive = immersive
                )
            )

            Log.d("DownloadWorker", "Got playback info: $playbackInfo")

            downloadRepository.updateDownloadStatus(id, DownloadStatus.DOWNLOADING)
            setForeground(getForegroundInfo())

            val downloadedFiles = playbackInfo.urls.mapIndexed { index, url ->
                val extension = if (playbackInfo.codec == "flac") "flac" else "m4a"
                val fileName = "${id}_part_${index}.${extension}"

                Log.d("DownloadWorker", "Downloading part $index from $url")

                val progress = (index + 1) * 100 / playbackInfo.urls.size
                setProgress(workDataOf("progress" to progress))
                downloadRepository.updateDownloadProgress(id, progress)
                setForeground(getForegroundInfo())

                downloadRepository.downloadFile(url, fileName).getOrThrow()
            }

            val finalFile = if (downloadedFiles.size > 1) {
                downloadRepository.updateDownloadStatus(id, DownloadStatus.MERGING)
                setForeground(getForegroundInfo())

                val outputFile =
                    "${id}_final.${if (playbackInfo.codec == "flac") "flac" else "m4a"}"
                downloadRepository.mergeFiles(downloadedFiles, outputFile, playbackInfo.codec)
                    .getOrThrow()
            } else {
                downloadedFiles.first()
            }

            if (downloadedFiles.size > 1) {
                downloadedFiles.forEach { File(it).delete() }
            }

            downloadRepository.updateDownloadStatus(id, DownloadStatus.COMPLETED)
            downloadRepository.updateDownloadProgress(id, 100)
            downloadRepository.updateDownloadFilePath(id, finalFile)

            Result.success()
        } catch (e: Exception) {
            Log.e("DownloadWorker", "Download failed", e)
            downloadRepository.updateDownloadStatus(id, DownloadStatus.FAILED)
            Result.failure()
        }
    }
}