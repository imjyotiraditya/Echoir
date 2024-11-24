package dev.jyotiraditya.echoir.domain.usecase

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.jyotiraditya.echoir.data.worker.DownloadWorker
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import javax.inject.Inject

class DownloadTrackUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val workManager: WorkManager
) {
    suspend operator fun invoke(track: SearchResult, config: QualityConfig) {
        val download = Download(
            id = track.id,
            title = track.title,
            artist = track.artists.joinToString(", "),
            cover = track.cover,
            quality = config.quality
        )

        downloadRepository.saveDownload(download)

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
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