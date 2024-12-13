package dev.jyotiraditya.echoir.presentation.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import dev.jyotiraditya.echoir.presentation.components.TrackCover
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.io.File
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch

@Composable
fun DownloadItem(
    download: Download,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    ListItem(
        modifier = modifier.clickable {
            coroutineScope.launch {
                val file = File(download.filePath)

                val uri: Uri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    file
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "audio/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val packageManager = context.packageManager
                val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

                if (activities.isNotEmpty()) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "No music player found", Toast.LENGTH_SHORT).show()
                }
            }
        },
        overlineContent = {
            Text(
                text = when (download.quality) {
                    "HI_RES_LOSSLESS" -> "HI-RES"
                    "LOSSLESS" -> "LOSSLESS"
                    "DOLBY_ATMOS" -> if (download.isAc4) "DOLBY ATMOS (AC-4)" else "DOLBY ATMOS (AC-3)"
                    "HIGH" -> "AAC 320"
                    "LOW" -> "AAC 96"
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
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                Text(
                    text = download.duration,
                    style = MaterialTheme.typography.bodySmall
                )
                if (download.explicit) {
                    Icon(
                        painter = painterResource(R.drawable.ic_explicit),
                        contentDescription = "Explicit",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    )
}