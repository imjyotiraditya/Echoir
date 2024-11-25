package dev.jyotiraditya.echoir.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jyotiraditya.echoir.presentation.screens.home.components.DownloadItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error ?: "Unknown error occurred",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        state.activeDownloads.isEmpty() && state.downloadHistory.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No downloads yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.activeDownloads.isNotEmpty()) {
                    item {
                        Text(
                            text = "Active Downloads",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(
                        items = state.activeDownloads,
                        key = { it.id }
                    ) { download ->
                        DownloadItem(download = download)
                    }
                }

                if (state.downloadHistory.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Download History",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(
                        items = state.downloadHistory,
                        key = { it.id }
                    ) { download ->
                        DownloadItem(download = download)
                    }
                }
            }
        }
    }
}