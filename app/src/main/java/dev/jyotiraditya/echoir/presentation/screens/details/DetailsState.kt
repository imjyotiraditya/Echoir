package dev.jyotiraditya.echoir.presentation.screens.details

import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.SearchResult

data class DetailsState(
    val item: SearchResult? = null,
    val type: String = "",
    val tracks: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val downloads: Map<Long, Map<String, Download>> = emptyMap()
)