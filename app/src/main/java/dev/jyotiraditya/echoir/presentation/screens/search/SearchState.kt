package dev.jyotiraditya.echoir.presentation.screens.search

import dev.jyotiraditya.echoir.domain.model.SearchResult

data class SearchState(
    val query: String = "",
    val searchType: SearchType = SearchType.TRACKS,
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class SearchType(val title: String) {
    TRACKS("Tracks"),
    ALBUMS("Albums")
}