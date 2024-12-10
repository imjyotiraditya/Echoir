package dev.jyotiraditya.echoir.domain.model

sealed class QualityConfig(
    val label: String,
    val quality: String,
    val ac4: Boolean = false,
    val immersive: Boolean = true,
    val summary: String
) {
    data object HiRes : QualityConfig(
        label = "Hi-Res",
        quality = "HI_RES_LOSSLESS",
        summary = "24-bit/up to 192kHz FLAC format with maximum audio quality"
    )

    data object Lossless : QualityConfig(
        label = "Lossless",
        quality = "LOSSLESS",
        summary = "16-bit/44.1kHz FLAC format with CD quality"
    )

    data object AAC320 : QualityConfig(
        label = "AAC 320",
        quality = "HIGH",
        summary = "High quality lossy compression with 320kbps bitrate, suitable for most listening"
    )

    data object AAC96 : QualityConfig(
        label = "AAC 96",
        quality = "LOW",
        summary = "Standard quality lossy compression with 96kbps bitrate, optimal for storage saving"
    )

    data object DolbyAtmosAC3 : QualityConfig(
        label = "Dolby Atmos (AC-3)",
        quality = "DOLBY_ATMOS",
        summary = "Enhanced AC-3 format with Dolby Atmos spatial audio"
    )

    data object DolbyAtmosAC4 : QualityConfig(
        label = "Dolby Atmos (AC-4)",
        quality = "DOLBY_ATMOS",
        ac4 = true,
        summary = "AC-4 format with Dolby Atmos spatial audio, improved efficiency"
    )
}