package dev.jyotiraditya.echoir.domain.model

sealed class QualityConfig(
    val label: String,
    val quality: String,
    val ac4: Boolean = false,
    val immersive: Boolean = true
) {
    data object HiRes : QualityConfig(
        label = "Hi-Res",
        quality = "HI_RES_LOSSLESS"
    )

    data object Lossless : QualityConfig(
        label = "Lossless",
        quality = "LOSSLESS"
    )

    data object DolbyAtmosAC3 : QualityConfig(
        label = "Dolby Atmos (AC-3)",
        quality = "DOLBY_ATMOS",
    )

    data object DolbyAtmosAC4 : QualityConfig(
        label = "Dolby Atmos (AC-4)",
        quality = "DOLBY_ATMOS",
        ac4 = true,
    )
}