package com.dn0ne.player.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dn0ne.player.app.data.online.YouTubeRepository
import com.dn0ne.player.app.domain.track.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val youTubeRepository: YouTubeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearch() {
        val query = _searchQuery.value
        if (query.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = youTubeRepository.search(query)
            result.onSuccess { tracks ->
                _searchResults.value = tracks
            }.onFailure {
                // Handle error
            }
            _isLoading.value = false
        }
    }

    fun resolveStreamUrl(track: Track, onResolved: (String) -> Unit) {
        val videoId = track.data.substringAfter("v=")
        if (videoId == track.data) { // Not a youtube url or invalid format
             onResolved(track.data) // Return original if parsing fails
             return
        }
        
        viewModelScope.launch {
            val result = youTubeRepository.getStreamUrl(videoId)
            result.onSuccess { url ->
               onResolved(url)
            }.onFailure {
                // error
            }
        }
    }
}
