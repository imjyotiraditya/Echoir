package dev.jyotiraditya.echoir.data.remote.mapper

import dev.jyotiraditya.echoir.data.remote.dto.SearchResultDto
import dev.jyotiraditya.echoir.domain.model.SearchResult

object SearchResultMapper {
    fun SearchResultDto.toDomain(): SearchResult =
        SearchResult(
            id = id,
            title = title,
            duration = duration,
            explicit = explicit,
            cover = cover,
            artists = artists,
            modes = modes,
            formats = formats
        )
}