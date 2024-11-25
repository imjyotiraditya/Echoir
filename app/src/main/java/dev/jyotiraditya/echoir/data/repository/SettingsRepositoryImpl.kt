package dev.jyotiraditya.echoir.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
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
    }

    override suspend fun getOutputDirectory(): String? {
        return context.dataStore.data.first()[PreferencesKeys.OUTPUT_DIRECTORY]
    }

    override suspend fun setOutputDirectory(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.OUTPUT_DIRECTORY] = uri
        }
    }
}