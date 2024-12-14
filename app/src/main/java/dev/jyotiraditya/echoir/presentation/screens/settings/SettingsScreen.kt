package dev.jyotiraditya.echoir.presentation.screens.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jyotiraditya.echoir.BuildConfig
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.data.utils.extensions.toDisplayPath
import dev.jyotiraditya.echoir.domain.model.Region
import dev.jyotiraditya.echoir.presentation.components.preferences.PreferenceCategory
import dev.jyotiraditya.echoir.presentation.components.preferences.PreferenceItem
import dev.jyotiraditya.echoir.presentation.components.preferences.PreferencePosition
import dev.jyotiraditya.echoir.presentation.screens.settings.components.CrucialSettingsDialog
import dev.jyotiraditya.echoir.presentation.screens.settings.components.FileNamingFormatDialog
import dev.jyotiraditya.echoir.presentation.screens.settings.components.MetadataFieldsDialog
import dev.jyotiraditya.echoir.presentation.screens.settings.components.RegionDialog

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var showFormatDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showRegionDialog by remember { mutableStateOf(false) }
    var showMetadataFieldsDialog by remember { mutableStateOf(false) }

    val dirPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.updateOutputDirectory(it.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dirPicker.launch(null)
        }
    }

    if (showFormatDialog) {
        FileNamingFormatDialog(
            selectedFormat = state.fileNamingFormat,
            onSelectFormat = { format ->
                viewModel.updateFileNamingFormat(format)
                showFormatDialog = false
            },
            onDismiss = { showFormatDialog = false }
        )
    }

    if (showResetDialog) {
        CrucialSettingsDialog(
            onDismiss = { showResetDialog = false },
            icon = Icons.Outlined.RestartAlt,
            title = "Reset Settings",
            description = "Are you sure you want to reset all settings to default? This cannot be undone.",
            confirmText = "Reset",
            onConfirm = {
                viewModel.resetSettings()
                showResetDialog = false
            }
        )
    }

    if (showClearDataDialog) {
        CrucialSettingsDialog(
            onDismiss = { showClearDataDialog = false },
            icon = Icons.Outlined.Delete,
            title = "Clear App Data",
            description = "This will clear whole download history and cache. This action cannot be undone.",
            confirmText = "Clear",
            onConfirm = {
                viewModel.clearData()
                showClearDataDialog = false
            }
        )
    }

    if (showRegionDialog) {
        RegionDialog(
            selectedRegion = state.region,
            onSelectRegion = { region ->
                viewModel.updateRegion(region)
                showRegionDialog = false
            },
            onDismiss = { showRegionDialog = false }
        )
    }

    if (showMetadataFieldsDialog) {
        MetadataFieldsDialog(
            selectedFields = state.selectedMetadataFields,
            onSelectFields = { fields ->
                viewModel.updateSelectedMetadataFields(fields)
            },
            onDismiss = { showMetadataFieldsDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            PreferenceCategory(title = "Content")
        }

        item {
            PreferenceItem(
                title = "Region",
                subtitle = "${Region.fromCode(state.region).displayName} (${state.region})",
                icon = Icons.Outlined.Public,
                onClick = { showRegionDialog = true },
                position = PreferencePosition.Single
            )
        }

        item {
            PreferenceCategory(title = "Storage")
        }

        item {
            PreferenceItem(
                title = "Download Location",
                subtitle = state.outputDirectory.toDisplayPath(context),
                icon = Icons.Outlined.Folder,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        dirPicker.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                },
                position = PreferencePosition.Top
            )
        }

        item {
            PreferenceItem(
                title = "Metadata Fields",
                subtitle = "${state.selectedMetadataFields.size} fields selected",
                icon = Icons.Outlined.EditNote,
                onClick = { showMetadataFieldsDialog = true },
                position = PreferencePosition.Middle
            )
        }

        item {
            PreferenceItem(
                title = "File Naming Format",
                subtitle = state.fileNamingFormat.displayName,
                icon = Icons.Outlined.TextFormat,
                onClick = { showFormatDialog = true },
                position = PreferencePosition.Bottom
            )
        }

        item {
            PreferenceCategory(title = "Data")
        }

        item {
            PreferenceItem(
                title = "Clear App Data",
                subtitle = "Clear downloads, cache, and history",
                icon = Icons.Outlined.Delete,
                onClick = { showClearDataDialog = true },
                position = PreferencePosition.Top
            )
        }

        item {
            PreferenceItem(
                title = "Reset Settings",
                subtitle = "Reset all settings to default",
                icon = Icons.Outlined.RestartAlt,
                onClick = { showResetDialog = true },
                position = PreferencePosition.Bottom
            )
        }

        item {
            PreferenceCategory(title = "About")
        }

        item {
            PreferenceItem(
                title = stringResource(R.string.app_name),
                subtitle = "Version ${BuildConfig.VERSION_NAME}",
                icon = Icons.Outlined.Info,
                position = PreferencePosition.Top,
            )
        }

        item {
            PreferenceItem(
                title = "Made with hate",
                subtitle = "by an exhausted panda running on spite and coffee",
                icon = painterResource(R.drawable.ic_panda),
                position = PreferencePosition.Middle
            )
        }

        item {
            PreferenceItem(
                title = "Telegram Channel",
                subtitle = "@ThisPandaCanTalk",
                icon = Icons.Outlined.Public,
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://t.me/ThisPandaCanTalk")
                    )
                    context.startActivity(intent)
                },
                position = PreferencePosition.Middle
            )
        }

        item {
            PreferenceItem(
                title = "Support Development on Ko-fi",
                subtitle = "Help to keep this project alive",
                icon = Icons.Outlined.LocalCafe,
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://ko-fi.com/imjyotiraditya")
                    )
                    context.startActivity(intent)
                },
                position = PreferencePosition.Bottom
            )
        }
    }
}