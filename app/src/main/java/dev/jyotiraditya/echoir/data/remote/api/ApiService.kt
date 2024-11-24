package dev.jyotiraditya.echoir.data.remote.api

import dev.jyotiraditya.echoir.BuildConfig
import dev.jyotiraditya.echoir.data.remote.dto.SearchResultDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApiService @Inject constructor() {
    private val client = KtorClient.client

    companion object {
        private const val BASE_URL = "https://echoir.vercel.app/api"
        private const val API_KEY = BuildConfig.API_KEY
    }

    suspend fun search(query: String, type: String): List<SearchResultDto> =
        withContext(Dispatchers.IO) {
            client.get("$BASE_URL/search") {
                parameter("query", query)
                parameter("type", type)
                header("X-API-Key", API_KEY)
            }.body()
        }

    suspend fun getAlbumTracks(albumId: Long): List<SearchResultDto> =
        withContext(Dispatchers.IO) {
            client.get("$BASE_URL/album/tracks") {
                parameter("id", albumId)
                header("X-API-Key", API_KEY)
            }.body()
        }
}