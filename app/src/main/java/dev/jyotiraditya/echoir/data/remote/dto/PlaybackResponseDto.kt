package dev.jyotiraditya.echoir.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlaybackResponseDto(
    val id: Long,
    val quality: String,
    val manifest: String,
    val bitDepth: Int? = null,
    val sampleRate: Int? = null,
    val urls: List<String>,
    val codec: String
)