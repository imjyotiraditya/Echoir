package dev.jyotiraditya.echoir.presentation.screens.home

import dev.jyotiraditya.echoir.domain.model.Download

data class HomeState(
    val activeDownloads: List<Download> = emptyList(),
    val downloadHistory: List<Download> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)