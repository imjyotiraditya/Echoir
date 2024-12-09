package dev.jyotiraditya.echoir.presentation.screens.search

import dev.jyotiraditya.echoir.domain.model.SearchResult

data class SearchState(
    val query: String = "",
    val searchType: SearchType = SearchType.TRACKS,
    val results: List<SearchResult> = emptyList(),
    val status: SearchStatus = SearchStatus.Empty,
    val error: String? = null
)

enum class SearchType(val title: String) {
    TRACKS("Tracks"),
    ALBUMS("Albums")
}

sealed class SearchStatus {
    data object Empty : SearchStatus()
    data object Ready : SearchStatus()
    data object Loading : SearchStatus()
    data object Success : SearchStatus()
    data object NoResults : SearchStatus()
    data object Error : SearchStatus()
}