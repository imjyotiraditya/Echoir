package dev.jyotiraditya.echoir.domain.model

data class SearchResult(
    val id: Long,
    val title: String,
    val duration: String,
    val explicit: Boolean,
    val cover: String?,
    val artists: List<String>,
    val modes: List<String>?,
    val formats: List<String>?
)