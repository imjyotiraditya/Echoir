package dev.jyotiraditya.echoir.domain.usecase

import dev.jyotiraditya.echoir.domain.model.FileNamingFormat
import dev.jyotiraditya.echoir.domain.model.MetadataField
import dev.jyotiraditya.echoir.domain.repository.SettingsRepository
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend fun getOutputDirectory(): String? = repository.getOutputDirectory()

    suspend fun setOutputDirectory(uri: String?) = repository.setOutputDirectory(uri)

    suspend fun getFileNamingFormat(): FileNamingFormat = repository.getFileNamingFormat()

    suspend fun setFileNamingFormat(format: FileNamingFormat) =
        repository.setFileNamingFormat(format)

    suspend fun getRegion(): String = repository.getRegion()

    suspend fun setRegion(region: String) = repository.setRegion(region)

    suspend fun getSelectedMetadataFields(): Set<MetadataField> =
        repository.getSelectedMetadataFields()

    suspend fun setSelectedMetadataFields(fields: Set<MetadataField>) =
        repository.setSelectedMetadataFields(fields)
}