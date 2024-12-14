package dev.jyotiraditya.echoir.domain.repository

import dev.jyotiraditya.echoir.domain.model.FileNamingFormat
import dev.jyotiraditya.echoir.domain.model.MetadataField

interface SettingsRepository {
    suspend fun getOutputDirectory(): String?
    suspend fun setOutputDirectory(uri: String?)
    suspend fun getFileNamingFormat(): FileNamingFormat
    suspend fun setFileNamingFormat(format: FileNamingFormat)
    suspend fun getRegion(): String
    suspend fun setRegion(region: String)
    suspend fun getSelectedMetadataFields(): Set<MetadataField>
    suspend fun setSelectedMetadataFields(fields: Set<MetadataField>)
}