package dev.jyotiraditya.echoir.domain.model

data class PlaybackResponse(
    val id: Long,
    val quality: String,
    val manifest: String,
    val bitDepth: Int? = null,
    val sampleRate: Int? = null,
    val urls: List<String>,
    val codec: String
)