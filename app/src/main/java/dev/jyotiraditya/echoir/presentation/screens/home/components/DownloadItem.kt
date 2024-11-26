package dev.jyotiraditya.echoir.presentation.screens.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.presentation.components.TrackCover

@Composable
fun DownloadItem(
    download: Download,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        overlineContent = {
            Text(
                text = when (download.quality) {
                    "HI_RES_LOSSLESS" -> "HI-RES"
                    "LOSSLESS" -> "LOSSLESS"
                    "DOLBY_ATMOS" -> if (download.isAc4) "DOLBY ATMOS (AC-4)" else "DOLBY ATMOS (AC-3)"
                    else -> "UNKNOWN"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        },
        headlineContent = {
            Text(
                text = download.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        supportingContent = {
            Text(
                text = download.artist,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        leadingContent = {
            TrackCover(
                url = download.cover,
                size = 56.dp
            )
        },
        trailingContent = {
            when (download.status) {
                DownloadStatus.QUEUED -> {
                    Text(
                        text = "Queued",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                DownloadStatus.DOWNLOADING -> {
                    CircularProgressIndicator(
                        progress = { download.progress / 100f },
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }

                DownloadStatus.MERGING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }

                DownloadStatus.COMPLETED -> {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                DownloadStatus.FAILED -> {
                    Icon(
                        imageVector = Icons.Outlined.Error,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}