package dev.jyotiraditya.echoir.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun TrackCover(
    url: String?,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .size(size)
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
                .size(size)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(
                    MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.08f)
                )
        )
    }
}