package dev.jyotiraditya.echoir.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "downloads")
data class Download(
    @PrimaryKey
    val downloadId: String = UUID.randomUUID().toString(),
    val trackId: Long,
    val title: String,
    val artist: String,
    val cover: String?,
    val quality: String,
    val duration: String,
    val explicit: Boolean,
    val progress: Int = 0,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val filePath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val format: String? = null,
    val albumId: Long? = null,
    val albumTitle: String? = null,
    val albumDirectory: String? = null,
    val isAc4: Boolean = false,
    val errorMessage: String? = null,
    val errorDetails: String? = null
)