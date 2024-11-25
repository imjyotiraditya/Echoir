package dev.jyotiraditya.echoir.domain.repository

interface SettingsRepository {
    suspend fun getOutputDirectory(): String?
    suspend fun setOutputDirectory(uri: String)
}