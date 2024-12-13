package dev.jyotiraditya.echoir.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.jyotiraditya.echoir.domain.model.FileNamingFormat
import dev.jyotiraditya.echoir.domain.model.MetadataCategory
import dev.jyotiraditya.echoir.domain.model.MetadataField
import dev.jyotiraditya.echoir.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {
    private object PreferencesKeys {
        val OUTPUT_DIRECTORY = stringPreferencesKey("output_directory")
        val FILE_NAMING_FORMAT = intPreferencesKey("file_naming_format")
        val REGION = stringPreferencesKey("region")
        val METADATA_FIELDS = stringSetPreferencesKey("metadata_fields")
    }

    override suspend fun getOutputDirectory(): String? {
        return context.dataStore.data.first()[PreferencesKeys.OUTPUT_DIRECTORY]
    }

    override suspend fun setOutputDirectory(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri == null) {
                preferences.remove(PreferencesKeys.OUTPUT_DIRECTORY)
            } else {
                preferences[PreferencesKeys.OUTPUT_DIRECTORY] = uri
            }
        }
    }

    override suspend fun getFileNamingFormat(): FileNamingFormat {
        val ordinal = context.dataStore.data.first()[PreferencesKeys.FILE_NAMING_FORMAT] ?: 0
        return FileNamingFormat.fromOrdinal(ordinal)
    }

    override suspend fun setFileNamingFormat(format: FileNamingFormat) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FILE_NAMING_FORMAT] = format.ordinal
        }
    }

    override suspend fun getRegion(): String {
        return context.dataStore.data.first()[PreferencesKeys.REGION] ?: "BR"
    }

    override suspend fun setRegion(region: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REGION] = region
        }
    }

    override suspend fun getSelectedMetadataFields(): Set<MetadataField> {
        return context.dataStore.data.first()[PreferencesKeys.METADATA_FIELDS]?.mapNotNull { key ->
            MetadataField.fromKey(key)
        }?.toSet() ?: getDefaultMetadataFields()
    }

    override suspend fun setSelectedMetadataFields(fields: Set<MetadataField>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.METADATA_FIELDS] = fields.map { it.key }.toSet()
        }
    }

    private fun getDefaultMetadataFields(): Set<MetadataField> = buildSet {
        addAll(MetadataField.entries.filter { it.category == MetadataCategory.CORE })
        addAll(MetadataField.entries.filter { it.category == MetadataCategory.STANDARD })
    }
}