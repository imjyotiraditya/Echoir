package dev.jyotiraditya.echoir.presentation.screens.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.domain.model.SearchResult

@Composable
fun QualitySection(
    result: SearchResult,
    onDownload: (QualityConfig) -> Unit
) {
    result.modes?.let { modes ->
        result.formats?.let { formats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (modes.contains("DOLBY_ATMOS") && formats.contains("DOLBY_ATMOS")) {
                    DownloadButton(
                        config = QualityConfig.DolbyAtmosAC3,
                        onClick = onDownload
                    )
                    DownloadButton(
                        config = QualityConfig.DolbyAtmosAC4,
                        onClick = onDownload
                    )
                }

                if (modes.contains("STEREO")) {
                    if (formats.contains("HIRES_LOSSLESS")) {
                        DownloadButton(
                            config = QualityConfig.HiRes,
                            onClick = onDownload
                        )
                    }
                    if (formats.contains("LOSSLESS") && !modes.contains("DOLBY_ATMOS")) {
                        DownloadButton(
                            config = QualityConfig.Lossless,
                            onClick = onDownload
                        )
                    }
                }
            }
        }
    }
}