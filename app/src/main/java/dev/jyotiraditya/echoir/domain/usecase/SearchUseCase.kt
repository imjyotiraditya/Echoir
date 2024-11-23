package dev.jyotiraditya.echoir.domain.usecase

import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.domain.repository.MusicRepository
import dev.jyotiraditya.echoir.presentation.screens.search.SearchType
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend fun searchTracks(query: String): List<SearchResult> =
        repository.search(query, SearchType.TRACKS)

    suspend fun searchAlbums(query: String): List<SearchResult> =
        repository.search(query, SearchType.ALBUMS)
}