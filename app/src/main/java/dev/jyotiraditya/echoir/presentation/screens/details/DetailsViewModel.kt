package dev.jyotiraditya.echoir.presentation.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.jyotiraditya.echoir.domain.model.SearchResult
import dev.jyotiraditya.echoir.domain.usecase.AlbumTracksUseCase
import dev.jyotiraditya.echoir.presentation.screens.search.SearchType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getAlbumTracksUseCase: AlbumTracksUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    fun initializeWithItem(type: String, item: SearchResult) {
        _state.update { it.copy(item = item) }
        if (type == SearchType.ALBUMS.name) {
            loadAlbumTracks(item.id)
        }
    }

    private fun loadAlbumTracks(albumId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val tracks = getAlbumTracksUseCase(albumId)
                _state.update { it.copy(tracks = tracks, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}