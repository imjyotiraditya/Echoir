package dev.jyotiraditya.echoir.domain.usecase

import dev.jyotiraditya.echoir.domain.repository.SettingsRepository
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend fun getOutputDirectory(): String? = repository.getOutputDirectory()

    suspend fun setOutputDirectory(uri: String) = repository.setOutputDirectory(uri)
}