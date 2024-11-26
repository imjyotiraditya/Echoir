package dev.jyotiraditya.echoir.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jyotiraditya.echoir.domain.usecase.SearchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
    }

    fun onSearchTypeChange(type: SearchType) {
        _state.update { it.copy(searchType = type) }
        if (_state.value.query.isNotEmpty()) {
            search()
        }
    }

    fun search() {
        val currentState = _state.value
        if (currentState.query.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val results = when (currentState.searchType) {
                    SearchType.TRACKS -> searchUseCase.searchTracks(currentState.query)
                    SearchType.ALBUMS -> searchUseCase.searchAlbums(currentState.query)
                }
                _state.update { it.copy(results = results, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun clearSearch() {
        _state.update {
            it.copy(query = "", results = emptyList(), error = null)
        }
    }
}