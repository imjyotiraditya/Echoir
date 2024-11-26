package dev.jyotiraditya.echoir.presentation.screens.details.components

import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.domain.model.QualityConfig

@Composable
fun DownloadButton(
    config: QualityConfig,
    onClick: (QualityConfig) -> Unit
) {
    val context = LocalContext.current

    FilterChip(
        selected = false,
        onClick = {
            onClick(config)
            Toast.makeText(
                context,
                "Started downloading in ${config.label} quality",
                Toast.LENGTH_SHORT
            ).show()
        },
        label = {
            if (config.quality == "DOLBY_ATMOS") {
                Text(
                    text = if (config.ac4) "AC4" else "EAC3",
                )
            } else {
                Text(text = config.label)
            }
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_download),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}