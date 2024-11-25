package dev.jyotiraditya.echoir.presentation.screens.details.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.domain.model.QualityConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadButton(
    config: QualityConfig,
    downloadStatus: DownloadStatus?,
    onClick: (QualityConfig) -> Unit
) {
    FilterChip(
        selected = downloadStatus != null,
        onClick = { onClick(config) },
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (downloadStatus) {
                    DownloadStatus.QUEUED -> Text("Queued")
                    DownloadStatus.DOWNLOADING -> Text("Downloading")
                    DownloadStatus.MERGING -> Text("Merging")
                    DownloadStatus.COMPLETED -> Text("Downloaded")
                    DownloadStatus.FAILED -> Text("Failed")
                    null -> {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(config.label)
                    }
                }
            }
        },
        leadingIcon = {
            when (downloadStatus) {
                DownloadStatus.DOWNLOADING, DownloadStatus.MERGING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
                DownloadStatus.COMPLETED -> {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                DownloadStatus.FAILED -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                else -> null
            }
        }
    )
}