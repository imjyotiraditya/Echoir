package dev.jyotiraditya.echoir.presentation.screens.search

import dev.jyotiraditya.echoir.domain.model.SearchResult

data class SearchState(
    val query: String = "",
    val searchType: SearchType = SearchType.TRACKS,
    val searchFilter: SearchFilter = SearchFilter(),
    val results: List<SearchResult> = emptyList(),
    val filteredResults: List<SearchResult> = emptyList(),
    val status: SearchStatus = SearchStatus.Empty,
    val error: String? = null
)

enum class SearchType(val title: String) {
    TRACKS("Tracks"),
    ALBUMS("Albums")
}

data class SearchFilter(
    val qualities: MutableList<SearchQuality> = mutableListOf(),
    var contentFilters: MutableList<SearchContentFilter> = mutableListOf()
)

enum class SearchContentFilter(
    val label: String,
    val explicit: Boolean
) {
    CLEAN("Clean", false),
    EXPLICIT("Explicit", true),
}

enum class SearchQuality(
    val label: String,
    val format: String
) {
    HIRES("Hi-Res", "HIRES_LOSSLESS"),
    LOSSLESS("Lossless", "LOSSLESS"),
    ATMOS("Dolby Atmos", "DOLBY_ATMOS"),
}

sealed class SearchStatus {
    data object Empty : SearchStatus()
    data object Ready : SearchStatus()
    data object Loading : SearchStatus()
    data object Success : SearchStatus()
    data object NoResults : SearchStatus()
    data object Error : SearchStatus()
}