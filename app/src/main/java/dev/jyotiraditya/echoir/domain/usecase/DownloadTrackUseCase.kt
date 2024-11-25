package dev.jyotiraditya.echoir.domain.usecase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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
        private const val CHANNEL_ID = "download_channel"
        private const val NOTIFICATION_ID = 1
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

    private fun createForegroundInfo(track: SearchResult): ForegroundInfo {
        createNotificationChannel()

        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Downloading ${track.title}")
            .setTicker("Downloading ${track.title}")
            .setSmallIcon(R.drawable.ic_download)
            .setOngoing(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    suspend operator fun invoke(track: SearchResult, config: QualityConfig) {
        val download = Download(
            id = track.id,
            title = track.title,
            artist = track.artists.joinToString(", "),
            cover = track.cover,
            quality = config.quality
        )

        downloadRepository.saveDownload(download)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(constraints)
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