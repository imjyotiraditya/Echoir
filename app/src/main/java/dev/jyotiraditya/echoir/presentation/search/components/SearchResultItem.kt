package dev.jyotiraditya.echoir.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.domain.model.SearchResult

@Composable
fun SearchResultItem(result: SearchResult) {
    ListItem(
        overlineContent = {
            result.formats?.let { formats ->
                Text(
                    text = when {
                        formats.contains("HIRES_LOSSLESS") -> "HI-RES"
                        formats.contains("LOSSLESS") -> "LOSSLESS"
                        formats.contains("DOLBY_ATMOS") -> "DOLBY"
                        else -> "UNKNOWN"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        },
        headlineContent = {
            Text(
                text = result.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        supportingContent = {
            Text(
                text = result.artists.joinToString(", "),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        },
        leadingContent = {
            Box {
                AsyncImage(
                    model = result.cover,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.extraSmall
                        ),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(
                            MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.08f)
                        )
                )
            }
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = result.duration,
                    style = MaterialTheme.typography.bodySmall
                )
                if (result.explicit) {
                    Icon(
                        painter = painterResource(R.drawable.ic_explicit),
                        contentDescription = "Explicit",
                        modifier = Modifier.size(16.dp),
                    )
                }
                result.formats?.let { formats ->
                    if (formats.contains("DOLBY_ATMOS")) {
                        Icon(
                            painter = painterResource(R.drawable.ic_dolby),
                            contentDescription = "Dolby Atmos",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    )
}