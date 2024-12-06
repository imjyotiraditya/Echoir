package dev.jyotiraditya.echoir.presentation.screens.search

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Explicit
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.jyotiraditya.echoir.R
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.presentation.components.EmptyStateMessage
import dev.jyotiraditya.echoir.presentation.components.TrackBottomSheet
import dev.jyotiraditya.echoir.presentation.navigation.Route
import dev.jyotiraditya.echoir.presentation.screens.search.components.SearchResultItem
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var selectedTrack by remember { mutableStateOf<SearchResult?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showFilterColumn by remember { mutableStateOf(false) }

    // hide filter chips when we start scrolling search results
    var filterColumnHeightPx by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (showFilterColumn && available.y < 0) {
                    showFilterColumn = false
                    return Offset(
                        x = 0f,
                        // consume the filter column height from the scroll delta
                        y = -minOf(filterColumnHeightPx, available.y.absoluteValue)
                    )
                }
                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                shape = MaterialTheme.shapes.small,
                placeholder = {
                    Text(
                        text = "Search ${state.searchType.title}...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Search",
                    )
                },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearSearch() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_clear),
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (state.status == SearchStatus.Ready) {
                            viewModel.search()
                            focusManager.clearFocus()
                        }
                    }
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchType.entries.forEach { type ->
                    FilterChip(
                        selected = state.searchType == type,
                        onClick = {
                            viewModel.onSearchTypeChange(type)
                            focusManager.clearFocus()
                        },
                        label = {
                            Text(
                                text = type.title,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = state.searchType == type,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                    tooltip = { PlainTooltip { Text("Filter results") } },
                    state = rememberTooltipState()
                ) {
                    IconButton(
                        onClick = { showFilterColumn = !showFilterColumn }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterAlt,
                            contentDescription = "Filter"
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showFilterColumn
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
                    .onGloballyPositioned { coordinates ->
                        filterColumnHeightPx = coordinates.size.height.toFloat()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AudioFile,
                        contentDescription = "Format",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Quality",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchQuality.entries.forEach { quality ->
                        var selected by remember {
                            mutableStateOf(state.searchFilter.qualities.contains(quality))
                        }
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selected = !selected
                                if (selected) {
                                    viewModel.onSearchFilterQualityAdded(quality)
                                } else {
                                    viewModel.onSearchFilterQualityRemoved(quality)
                                }
                            },
                            label = {
                                Text(
                                    text = quality.label,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Explicit,
                        contentDescription = "Content",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Content",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchContentFilter.entries.forEach { contentFilter ->
                        var selected by remember {
                            mutableStateOf(
                                state.searchFilter.contentFilters.contains(contentFilter)
                            )
                        }
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selected = !selected
                                if (selected) {
                                    viewModel.onSearchContentFilterAdded(contentFilter)
                                } else {
                                    viewModel.onSearchContentFilterRemoved(contentFilter)
                                }
                            },
                            label = {
                                Text(
                                    text = contentFilter.label,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        when (state.status) {
            SearchStatus.Empty, SearchStatus.Ready -> {
                EmptyStateMessage(
                    title = "Search for music",
                    description = "Enter an artist, album, or track name to start searching.",
                    painter = painterResource(R.drawable.ic_search)
                )
            }

            SearchStatus.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator()
                }
            }

            SearchStatus.Success -> {
                if (state.filteredResults.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .nestedScroll(nestedScrollConnection)
                    ) {
                        items(state.filteredResults) { result ->
                            SearchResultItem(
                                result = result,
                                onClick = {
                                    if (state.searchType == SearchType.TRACKS) {
                                        selectedTrack = result
                                        showBottomSheet = true
                                    } else {
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("result", result)
                                        navController.navigate(
                                            Route.Search.Details().createPath(
                                                type = state.searchType.name,
                                                id = result.id
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                } else {
                    EmptyStateMessage(
                        title = "No results found with the given filters",
                        description = "Try removing the filters or searching for something else.",
                        painter = painterResource(R.drawable.ic_search)
                    )
                }
            }

            SearchStatus.NoResults -> {
                EmptyStateMessage(
                    title = "No results found",
                    description = "Try searching for something else.",
                    painter = painterResource(R.drawable.ic_search)
                )
            }

            SearchStatus.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = state.error ?: "An unknown error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        FilledTonalButton(
                            onClick = { viewModel.search() }
                        ) {
                            Text("Retry")
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