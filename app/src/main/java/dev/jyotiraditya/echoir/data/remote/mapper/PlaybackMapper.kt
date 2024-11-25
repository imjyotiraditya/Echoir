package dev.jyotiraditya.echoir.data.remote.mapper

import dev.jyotiraditya.echoir.data.remote.dto.PlaybackResponseDto
import dev.jyotiraditya.echoir.domain.model.PlaybackResponse

object PlaybackMapper {
    fun PlaybackResponseDto.toDomain(): PlaybackResponse =
        PlaybackResponse(
            id = id,
            quality = quality,
            manifest = manifest,
            bitDepth = bitDepth,
            sampleRate = sampleRate,
            urls = urls,
            codec = codec
        )
}