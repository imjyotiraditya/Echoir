package dev.jyotiraditya.echoir.presentation.screens.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.domain.model.SearchResult

@Composable
fun QualitySection(
    result: SearchResult,
    downloadStatus: DownloadStatus?,
    onDownload: (QualityConfig) -> Unit
) {
    result.modes?.let { modes ->
        result.formats?.let { formats ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (modes.contains("DOLBY_ATMOS") && formats.contains("DOLBY_ATMOS")) {
                    Text(
                        text = "Dolby Formats:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DownloadButton(
                            config = QualityConfig.DolbyAtmosAC3,
                            downloadStatus = downloadStatus,
                            onClick = onDownload
                        )
                        DownloadButton(
                            config = QualityConfig.DolbyAtmosAC4,
                            downloadStatus = downloadStatus,
                            onClick = onDownload
                        )
                    }
                }

                if (modes.contains("STEREO")) {
                    Text(
                        text = "Stereo Formats:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (formats.contains("HIRES_LOSSLESS")) {
                            DownloadButton(
                                config = QualityConfig.HiRes,
                                downloadStatus = downloadStatus,
                                onClick = onDownload
                            )
                        }
                        if (formats.contains("LOSSLESS") && !modes.contains("DOLBY_ATMOS")) {
                            DownloadButton(
                                config = QualityConfig.Lossless,
                                downloadStatus = downloadStatus,
                                onClick = onDownload
                            )
                        }
                    }
                }
            }
        }
    }
}