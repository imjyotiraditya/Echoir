package dev.jyotiraditya.echoir.presentation.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.jyotiraditya.echoir.data.local.dao.DownloadDao
import dev.jyotiraditya.echoir.domain.model.FileNamingFormat
import dev.jyotiraditya.echoir.domain.model.MetadataField
import dev.jyotiraditya.echoir.domain.usecase.SettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val workManager: WorkManager,
    private val downloadDao: DownloadDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val dir = settingsUseCase.getOutputDirectory()
            val format = settingsUseCase.getFileNamingFormat()
            val region = settingsUseCase.getRegion()
            val metadataFields = settingsUseCase.getSelectedMetadataFields()
            _state.update {
                it.copy(
                    outputDirectory = dir,
                    fileNamingFormat = format,
                    region = region,
                    selectedMetadataFields = metadataFields
                )
            }
        }
    }

    fun updateOutputDirectory(uri: String) {
        viewModelScope.launch {
            settingsUseCase.setOutputDirectory(uri)
            _state.update {
                it.copy(
                    outputDirectory = uri
                )
            }
        }
    }

    fun updateFileNamingFormat(format: FileNamingFormat) {
        viewModelScope.launch {
            settingsUseCase.setFileNamingFormat(format)
            _state.update {
                it.copy(
                    fileNamingFormat = format
                )
            }
        }
    }

    fun updateRegion(region: String) {
        viewModelScope.launch {
            settingsUseCase.setRegion(region)
            _state.update {
                it.copy(
                    region = region
                )
            }
        }
    }

    fun updateSelectedMetadataFields(fields: Set<MetadataField>) {
        viewModelScope.launch {
            settingsUseCase.setSelectedMetadataFields(fields)
            _state.update {
                it.copy(
                    selectedMetadataFields = fields
                )
            }
        }
    }

    fun clearData() {
        viewModelScope.launch {
            workManager.cancelAllWork()
            downloadDao.deleteAll()
            context.cacheDir.deleteRecursively()
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            settingsUseCase.setOutputDirectory(null)
            settingsUseCase.setFileNamingFormat(FileNamingFormat.TITLE_ONLY)
            settingsUseCase.setRegion("BR")

            _state.update {
                it.copy(
                    outputDirectory = null,
                    fileNamingFormat = FileNamingFormat.TITLE_ONLY,
                    region = "BR"
                )
            }
        }
    }
}