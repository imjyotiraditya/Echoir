package dev.jyotiraditya.echoir.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.domain.model.QualityConfig

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DownloadOptions(
    formats: List<String>?,
    modes: List<String>?,
    onOptionSelected: (QualityConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val downloadOptions = buildList {
        if (!formats.isNullOrEmpty() && !modes.isNullOrEmpty()) {
            val hasDolbyAtmos = modes.contains("DOLBY_ATMOS") && formats.contains("DOLBY_ATMOS")
            val hasStereo = modes.contains("STEREO")

            if (hasDolbyAtmos) {
                add(QualityConfig.DolbyAtmosAC3)
                add(QualityConfig.DolbyAtmosAC4)
            }

            if (hasStereo && formats.contains("HIRES_LOSSLESS")) {
                add(QualityConfig.HiRes)
            }

            if (hasStereo && !hasDolbyAtmos) {
                if (formats.contains("LOSSLESS")) {
                    add(QualityConfig.Lossless)
                }
                add(QualityConfig.AAC320)
                add(QualityConfig.AAC96)
            }
        }
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        downloadOptions.forEach { config ->
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(
                            text = config.summary
                        )
                    }
                },
                state = rememberTooltipState()
            ) {
                FilterChip(
                    selected = false,
                    onClick = { onOptionSelected(config) },
                    label = {
                        Text(
                            text = when (config.quality) {
                                "DOLBY_ATMOS" -> if (config.ac4) "AC4" else "EAC3"
                                else -> config.label
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_download),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        iconColor = MaterialTheme.colorScheme.onSurface,
                        selectedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        enabled = true,
                        selected = false
                    ),
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}