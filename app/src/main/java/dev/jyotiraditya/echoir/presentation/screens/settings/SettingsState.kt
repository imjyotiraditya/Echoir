package dev.jyotiraditya.echoir.presentation.screens.settings

import dev.jyotiraditya.echoir.domain.model.FileNamingFormat
import dev.jyotiraditya.echoir.domain.model.MetadataField

data class SettingsState(
    val outputDirectory: String? = null,
    val fileNamingFormat: FileNamingFormat = FileNamingFormat.TITLE_ONLY,
    val region: String = "BR",
    val selectedMetadataFields: Set<MetadataField> = MetadataField.entries.toSet()
)