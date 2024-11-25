package dev.jyotiraditya.echoir.domain.repository

import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.presentation.screens.search.SearchType

interface MusicRepository {
    suspend fun search(query: String, type: SearchType): List<SearchResult>
    suspend fun getAlbumTracks(albumId: Long): List<SearchResult>
}