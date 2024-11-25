package dev.jyotiraditya.echoir.data.repository

import dev.jyotiraditya.echoir.data.remote.api.ApiService
import dev.jyotiraditya.echoir.data.remote.mapper.SearchResultMapper.toDomain
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.domain.repository.MusicRepository
import dev.jyotiraditya.echoir.presentation.screens.search.SearchType
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MusicRepository {
    override suspend fun search(query: String, type: SearchType): List<SearchResult> =
        apiService.search(query, type.name.lowercase())
            .map { it.toDomain() }

    override suspend fun getAlbumTracks(albumId: Long): List<SearchResult> =
        apiService.getAlbumTracks(albumId)
            .map { it.toDomain() }
}