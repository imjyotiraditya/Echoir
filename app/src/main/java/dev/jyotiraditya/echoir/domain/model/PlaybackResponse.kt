package dev.jyotiraditya.echoir.domain.model

data class PlaybackResponse(
    val id: Long,
    val quality: String,
    val manifest: String,
    val bitDepth: Int,
    val sampleRate: Int,
    val urls: List<String>,
    val codec: String
)