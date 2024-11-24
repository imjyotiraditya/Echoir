package dev.jyotiraditya.echoir.data.local.converter

import androidx.room.TypeConverter
import dev.jyotiraditya.echoir.domain.model.DownloadStatus

class Converters {
    @TypeConverter
    fun fromDownloadStatus(value: DownloadStatus): String = value.name

    @TypeConverter
    fun toDownloadStatus(value: String): DownloadStatus = DownloadStatus.valueOf(value)
}
