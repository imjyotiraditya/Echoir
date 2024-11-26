package dev.jyotiraditya.echoir.domain.usecase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.data.worker.DownloadWorker
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import javax.inject.Inject

class DownloadTrackUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val workManager: WorkManager,
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "download_channel"
        private const val NOTIFICATION_ID_OFFSET = 10000
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Downloads",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Used for music downloads"
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun createForegroundInfo(
        id: Long,
        title: String,
        progress: Int,
        indeterminate: Boolean
    ): ForegroundInfo {
        createNotificationChannel()

        val notificationId = (id % Int.MAX_VALUE).toInt() + NOTIFICATION_ID_OFFSET

        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Downloading $title")
            .setTicker("Downloading $title")
            .setSmallIcon(R.drawable.ic_download)
            .setOngoing(true)
            .setProgress(100, progress, indeterminate)
            .setGroup("downloads")
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                }
            }
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                notificationId,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    suspend operator fun invoke(track: SearchResult, config: QualityConfig) {
        val download = Download(
            id = track.id,
            title = track.title,
            artist = track.artists.joinToString(", "),
            cover = track.cover,
            quality = config.quality,
            isAc4 = config.ac4
        )

        downloadRepository.saveDownload(download)

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                workDataOf(
                    "id" to track.id,
                    "quality" to config.quality,
                    "ac4" to config.ac4,
                    "immersive" to config.immersive
                )
            )
            .build()

        workManager.enqueueUniqueWork(
            "download_${track.id}",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}