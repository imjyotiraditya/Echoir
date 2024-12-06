package dev.jyotiraditya.echoir.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jyotiraditya.echoir.domain.model.DownloadRequest
import dev.jyotiraditya.echoir.domain.model.QualityConfig
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.domain.usecase.ProcessDownloadUseCase
import dev.jyotiraditya.echoir.domain.usecase.SearchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val processDownloadUseCase: ProcessDownloadUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onQueryChange(query: String) {
        _state.update {
            it.copy(
                query = query,
                status = when {
                    query.isEmpty() -> SearchStatus.Empty
                    else -> SearchStatus.Ready
                }
            )
        }
    }

    fun onSearchTypeChange(type: SearchType) {
        _state.update {
            it.copy(
                searchType = type
            )
        }
        if (_state.value.query.isNotEmpty()) {
            search()
        }
    }

    fun onSearchFilterQualityAdded(quality: SearchQuality) {
        _state.update {
            it.apply { searchFilter.qualities.add(quality) }
        }
        onSearchFilterChanged()
    }

    fun onSearchFilterQualityRemoved(quality: SearchQuality) {
        _state.update {
            it.apply { searchFilter.qualities.remove(quality) }
        }
        onSearchFilterChanged()
    }

    fun onSearchContentFilterAdded(contentFilter: SearchContentFilter) {
        _state.update {
            it.apply { searchFilter.contentFilters.add(contentFilter) }
        }
        onSearchFilterChanged()
    }

    fun onSearchContentFilterRemoved(contentFilter: SearchContentFilter) {
        _state.update {
            it.apply { searchFilter.contentFilters.remove(contentFilter) }
        }
        onSearchFilterChanged()
    }

    private fun onSearchFilterChanged() {
        if (_state.value.results.isNotEmpty()) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        filteredResults = searchUseCase.filterSearchResults(
                            _state.value.results,
                            _state.value.searchFilter
                        )
                    )
                }
            }
        }
    }

    fun search() {
        val currentState = _state.value

        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(
                        status = SearchStatus.Loading
                    )
                }

                val results = when (currentState.searchType) {
                    SearchType.TRACKS -> searchUseCase.searchTracks(currentState.query)
                    SearchType.ALBUMS -> searchUseCase.searchAlbums(currentState.query)
                }

                _state.update {
                    it.copy(
                        results = results,
                        filteredResults = searchUseCase.filterSearchResults(
                            results,
                            _state.value.searchFilter
                        ),
                        status = if (results.isEmpty()) SearchStatus.NoResults else SearchStatus.Success
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message,
                        status = SearchStatus.Error
                    )
                }
            }
        }
    }

    fun downloadTrack(track: SearchResult, config: QualityConfig) {
        viewModelScope.launch {
            processDownloadUseCase(
                DownloadRequest.Track(
                    track = track,
                    config = config
                )
            )
        }
    }

    fun clearSearch() {
        _state.update {
            it.copy(
                query = "",
                results = emptyList(),
                error = null,
                status = SearchStatus.Empty
            )
        }
    }
}