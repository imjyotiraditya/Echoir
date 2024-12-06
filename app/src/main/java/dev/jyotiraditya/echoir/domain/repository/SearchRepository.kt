package dev.jyotiraditya.echoir.domain.repository

import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.presentation.screens.search.SearchFilter
import dev.jyotiraditya.echoir.presentation.screens.search.SearchType

interface SearchRepository {
    suspend fun search(query: String, type: SearchType): List<SearchResult>
    suspend fun getAlbumTracks(albumId: Long): List<SearchResult>
    suspend fun filterSearchResults(
        results: List<SearchResult>,
        filter: SearchFilter
    ) : List<SearchResult>
}