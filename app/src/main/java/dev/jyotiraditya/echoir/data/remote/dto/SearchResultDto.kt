package dev.jyotiraditya.echoir.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    val id: Long,
    val title: String,
    val duration: String,
    val explicit: Boolean,
    val cover: String?,
    val artists: List<String>,
    val modes: List<String>?,
    val formats: List<String>?
)