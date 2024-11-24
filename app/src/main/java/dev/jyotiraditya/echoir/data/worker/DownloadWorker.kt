package dev.jyotiraditya.echoir.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.domain.model.PlaybackRequest
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import java.io.File

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadRepository: DownloadRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val id = inputData.getLong("id", 0)
        val quality = inputData.getString("quality") ?: return Result.failure()
        val ac4 = inputData.getBoolean("ac4", false)
        val immersive = inputData.getBoolean("immersive", false)

        return try {
            val playbackInfo = downloadRepository.getPlaybackInfo(
                PlaybackRequest(
                    id,
                    quality,
                    ac4 = ac4,
                    immersive = immersive
                )
            )

            downloadRepository.updateDownloadStatus(id, DownloadStatus.DOWNLOADING)

            val downloadedFiles = playbackInfo.urls.mapIndexed { index, url ->
                val extension = if (playbackInfo.codec == "flac") "flac" else "m4a"
                val fileName = "${id}_part_${index}.${extension}"

                setProgress(workDataOf("progress" to ((index + 1) * 100 / playbackInfo.urls.size)))
                downloadRepository.updateDownloadProgress(
                    id,
                    (index + 1) * 100 / playbackInfo.urls.size
                )

                downloadRepository.downloadFile(url, fileName).getOrThrow()
            }

            val finalFile = if (downloadedFiles.size > 1) {
                downloadRepository.updateDownloadStatus(id, DownloadStatus.MERGING)
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
            downloadRepository.updateDownloadStatus(id, DownloadStatus.FAILED)
            Result.failure()
        }
    }
}