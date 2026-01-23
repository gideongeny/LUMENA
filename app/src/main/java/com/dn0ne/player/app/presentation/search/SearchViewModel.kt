package com.dn0ne.player.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dn0ne.player.app.data.online.YouTubeRepository
import com.dn0ne.player.app.data.repository.TrackRepository
import com.dn0ne.player.app.domain.track.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val youTubeRepository: YouTubeRepository,
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        // Auto-search as user types with 500ms debounce
        viewModelScope.launch {
            searchQuery
                .debounce(500) // Wait 500ms after user stops typing
                .collect { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else {
                        // Clear results when query is empty
                        _searchResults.value = emptyList()
                        _errorMessage.value = null
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _errorMessage.value = null
    }

    fun onSearch() {
        // Manual search trigger (from search button)
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Search local tracks first (instant)
                val localTracks = trackRepository.getTracks().filter { track ->
                    track.title?.contains(query, ignoreCase = true) == true ||
                    track.artist?.contains(query, ignoreCase = true) == true ||
                    track.album?.contains(query, ignoreCase = true) == true
                }
                
                android.util.Log.d("SearchViewModel", "Local search for '$query': ${localTracks.size} results")
                
                // Show local results immediately
                _searchResults.value = localTracks
                
                // Search YouTube in parallel
                launch {
                    try {
                        val result = youTubeRepository.search(query)
                        result.onSuccess { youtubeTracks ->
                            // Append YouTube results to local results
                            _searchResults.value = localTracks + youtubeTracks
                            android.util.Log.d("SearchViewModel", "Combined results: ${localTracks.size} local + ${youtubeTracks.size} YouTube")
                        }.onFailure { error ->
                            // Keep local results even if YouTube fails
                            android.util.Log.e("SearchViewModel", "YouTube search failed, showing ${localTracks.size} local results", error)
                            if (localTracks.isEmpty()) {
                                _errorMessage.value = "YouTube search failed: ${error.message}"
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SearchViewModel", "YouTube search exception", e)
                        if (localTracks.isEmpty()) {
                            _errorMessage.value = "Search error: ${e.message}"
                        }
                    }
                }
                
                if (localTracks.isEmpty()) {
                    _errorMessage.value = "Searching online..."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search error: ${e.message}"
                android.util.Log.e("SearchViewModel", "Search exception", e)
            }
            
            _isLoading.value = false
        }
    }

    fun resolveStreamUrl(track: Track, onResolved: (String) -> Unit) {
        // Check if it's a YouTube track (online) or local track
        if (track.data.startsWith("http")) {
            // YouTube track - resolve stream URL
            val videoId = YouTubeRepository.extractVideoId(track.data)
            if (videoId == track.data) {
                onResolved(track.data)
                return
            }
            
            viewModelScope.launch {
                val result = youTubeRepository.getStreamUrl(videoId)
                result.onSuccess { url ->
                   onResolved(url)
                }.onFailure {
                    // Fallback to original URL
                    onResolved(track.data)
                }
            }
        } else {
            // Local track - use data path directly
            onResolved(track.data)
        }
    }
}
