package dev.jyotiraditya.echoir.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaybackRequest(
    val id: Long,
    val quality: String,
    val country: String = "US",
    val ac4: Boolean = false,
    val immersive: Boolean = false
)