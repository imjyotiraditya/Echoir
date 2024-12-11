package dev.jyotiraditya.echoir.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.jyotiraditya.echoir.data.notification.DownloadNotificationManager
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import java.util.UUID

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadRepository: DownloadRepository,
    private val notificationManager: DownloadNotificationManager,
    private val queueManager: DownloadQueueManager
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_DOWNLOAD_ID = "download_id"
        const val KEY_TRACK_ID = "track_id"
        const val KEY_QUALITY = "quality"
        const val KEY_AC4 = "ac4"
        const val KEY_IMMERSIVE = "immersive"
        const val KEY_PROGRESS = "progress"
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val downloadId =
            inputData.getString(KEY_DOWNLOAD_ID) ?: return createDefaultForegroundInfo()
        val download = downloadRepository.getDownloadById(downloadId)
        val title = download?.title ?: "Unknown Track"
        val isMerging = download?.status == DownloadStatus.MERGING
        val progress = download?.progress ?: 0

        return notificationManager.createDownloadNotification(
            downloadId = downloadId,
            title = if (isMerging) "Processing $title" else "Downloading $title",
            progress = progress,
            indeterminate = isMerging
        )
    }

    private fun createDefaultForegroundInfo(): ForegroundInfo {
        return notificationManager.createDownloadNotification(
            downloadId = UUID.randomUUID().toString(),
            title = "Downloading",
            progress = 0,
            indeterminate = false
        )
    }

    override suspend fun doWork(): Result {
        val downloadId = inputData.getString(KEY_DOWNLOAD_ID) ?: return Result.failure()
        val trackId = inputData.getLong(KEY_TRACK_ID, -1)
        if (trackId == -1L) return Result.failure()

        val quality = inputData.getString(KEY_QUALITY) ?: return Result.failure()
        val ac4 = inputData.getBoolean(KEY_AC4, false)
        val immersive = inputData.getBoolean(KEY_IMMERSIVE, false)

        return try {
            val download = downloadRepository.getDownloadById(downloadId)
            val result = downloadRepository.processDownload(
                downloadId = downloadId,
                trackId = trackId,
                quality = quality,
                ac4 = ac4,
                immersive = immersive
            ) { progress ->
                setProgress(workDataOf(KEY_PROGRESS to progress))
                downloadRepository.updateDownloadProgress(downloadId, progress)

                // Update notification with current progress
                download?.let {
                    notificationManager.updateDownloadProgress(
                        downloadId = downloadId,
                        title = it.title,
                        progress = progress,
                        indeterminate = false
                    )
                }
            }

            if (result.isSuccess) {
                download?.let {
                    notificationManager.showCompletionNotification(
                        downloadId = downloadId,
                        title = it.title
                    )
                }
                Result.success()
            } else {
                download?.let {
                    notificationManager.showErrorNotification(
                        downloadId = downloadId,
                        title = it.title
                    )
                }
                Result.failure()
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown error"
            val errorDetails = e.stackTraceToString()

            val download = downloadRepository.getDownloadById(downloadId)
            download?.let {
                downloadRepository.saveDownload(
                    it.copy(
                        status = DownloadStatus.FAILED,
                        errorMessage = errorMessage,
                        errorDetails = errorDetails
                    )
                )
                notificationManager.showErrorNotification(
                    downloadId = downloadId,
                    title = it.title
                )
            }
            Result.failure()
        } finally {
            queueManager.decrementActiveDownloads()

            if (queueManager.canStartNewDownload()) {
                val nextDownload = queueManager.dequeueDownload()
                if (nextDownload != null) {
                    enqueueDownloadWork(
                        workManager = WorkManager.getInstance(applicationContext),
                        download = nextDownload.download,
                        config = nextDownload.config,
                        queueManager = queueManager
                    )
                }
            }
        }
    }
}