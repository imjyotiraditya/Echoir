package dev.jyotiraditya.echoir.presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.presentation.screens.details.components.QualitySection
import dev.jyotiraditya.echoir.presentation.screens.search.SearchType

@Composable
fun DetailsScreen(
    type: String,
    result: SearchResult,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(result) {
        viewModel.initializeWithItem(type, result)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box {
                        AsyncImage(
                            model = result.cover?.replace("80x80", "160x160"),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                    shape = MaterialTheme.shapes.medium
                                ),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.08f)
                                )
                        )
                    }

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

                QualitySection(
                    result = result,
                    downloadStatus = state.downloadStatus,
                    onDownload = { config -> viewModel.downloadTrack(result, config) }
                )

                if (type == SearchType.TRACKS.name) {
                    result.modes?.let { modes ->
                        if (modes.isNotEmpty()) {
                            Text(
                                text = "Available in: ${modes.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        if (type == SearchType.ALBUMS.name) {
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            when {
                state.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                state.error != null -> {
                    item {
                        Text(
                            text = state.error ?: "Unknown error occurred",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                state.tracks.isNotEmpty() -> {
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
                            leadingContent = {
                                Text(
                                    text = "${state.tracks.indexOf(track) + 1}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            supportingContent = {
                                QualitySection(
                                    result = track,
                                    downloadStatus = state.downloadStatus,
                                    onDownload = { config ->
                                        viewModel.downloadTrack(
                                            track,
                                            config
                                        )
                                    }
                                )
                            },
                            trailingContent = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (track.explicit) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_explicit),
                                            contentDescription = "Explicit",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = track.duration,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}