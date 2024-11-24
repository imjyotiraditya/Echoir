package dev.jyotiraditya.echoir.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class Download(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val cover: String?,
    val quality: String,
    val progress: Int = 0,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val filePath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val format: String? = null,
    val albumId: Long? = null
)