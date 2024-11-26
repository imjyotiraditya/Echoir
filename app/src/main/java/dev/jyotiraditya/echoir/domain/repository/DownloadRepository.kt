package dev.jyotiraditya.echoir.domain.repository

import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.domain.model.PlaybackRequest
import dev.jyotiraditya.echoir.domain.model.PlaybackResponse
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    suspend fun getPlaybackInfo(request: PlaybackRequest): PlaybackResponse
    suspend fun downloadFile(url: String, fileName: String): Result<String>
    suspend fun mergeFiles(files: List<String>, outputFile: String, codec: String): Result<String>
    suspend fun saveDownload(download: Download)
    suspend fun updateDownloadProgress(id: Long, progress: Int)
    suspend fun updateDownloadStatus(id: Long, status: DownloadStatus)
    suspend fun updateDownloadFilePath(id: Long, filePath: String)
    suspend fun deleteDownload(download: Download)
    suspend fun getDownloadById(id: Long): Download?
    suspend fun getDownloadsByTrackId(id: Long): List<Download>
    suspend fun getDownloadsByAlbumId(albumId: Long): List<Download>
    fun getActiveDownloads(): Flow<List<Download>>
    fun getDownloadHistory(): Flow<List<Download>>
}