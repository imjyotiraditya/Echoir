package dev.jyotiraditya.echoir.presentation.screens.details

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.presentation.components.TrackBottomSheet
import dev.jyotiraditya.echoir.presentation.components.TrackCover

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailsScreen(
    result: SearchResult,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var selectedTrack by remember { mutableStateOf<SearchResult?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val downloadOptions = remember(result) {
        mutableListOf<QualityConfig>().apply {
            result.modes?.let { modes ->
                result.formats?.let { formats ->
                    if (modes.contains("DOLBY_ATMOS") && formats.contains("DOLBY_ATMOS")) {
                        add(QualityConfig.DolbyAtmosAC3)
                        add(QualityConfig.DolbyAtmosAC4)
                    }
                    if (modes.contains("STEREO")) {
                        if (formats.contains("HIRES_LOSSLESS")) {
                            add(QualityConfig.HiRes)
                        }
                        if (formats.contains("LOSSLESS") && !modes.contains("DOLBY_ATMOS")) {
                            add(QualityConfig.Lossless)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(result) {
        viewModel.initializeWithItem(result)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TrackCover(
                    url = result.cover?.replace("80x80", "160x160"),
                    size = 120.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = result.artists.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (result.formats?.contains("DOLBY_ATMOS") == true) {
                            Icon(
                                painter = painterResource(R.drawable.ic_dolby),
                                contentDescription = "Dolby Atmos",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (result.explicit) {
                            Icon(
                                painter = painterResource(R.drawable.ic_explicit),
                                contentDescription = "Explicit",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = result.duration,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                downloadOptions.forEach { config ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            viewModel.downloadAlbum(config)
                            Toast.makeText(
                                context,
                                "Started downloading in ${config.label} quality",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
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

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ContainedLoadingIndicator()
                    }
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Unknown error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                state.tracks.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.tracks,
                            key = { it.id }
                        ) { track ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = track.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        text = track.artists.joinToString(", "),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                leadingContent = {
                                    Text(
                                        text = String.format(
                                            java.util.Locale(Locale.current.language),
                                            "%02d",
                                            state.tracks.indexOf(track) + 1
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                trailingContent = {
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = track.duration,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        if (track.explicit) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_explicit),
                                                contentDescription = "Explicit",
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTrack = track
                                        showBottomSheet = true
                                    },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet && selectedTrack != null) {
        TrackBottomSheet(
            track = selectedTrack!!,
            onDownload = { config ->
                viewModel.downloadTrack(selectedTrack!!, config)
                Toast.makeText(
                    context,
                    "Started downloading in ${config.label} quality",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { showBottomSheet = false }
        )
    }
}