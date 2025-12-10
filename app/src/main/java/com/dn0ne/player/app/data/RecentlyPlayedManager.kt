package com.dn0ne.player.app.data

import android.content.Context
import com.dn0ne.player.app.domain.track.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RecentlyPlayedManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("recently-played", Context.MODE_PRIVATE)
    private val recentlyPlayedKey = "recently_played"
    private val maxItems = 50

    private val _recentlyPlayed = MutableStateFlow<List<Track>>(loadRecentlyPlayed())
    val recentlyPlayed = _recentlyPlayed.asStateFlow()

    private fun loadRecentlyPlayed(): List<Track> {
        val json = sharedPreferences.getString(recentlyPlayedKey, null) ?: return emptyList()
        return try {
            Json.decodeFromString<List<Track>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addTrack(track: Track) {
        _recentlyPlayed.update { currentList ->
            val updated = (listOf(track) + currentList.filter { it.uri != track.uri })
                .take(maxItems)
            saveRecentlyPlayed(updated)
            updated
        }
    }

    private fun saveRecentlyPlayed(tracks: List<Track>) {
        with(sharedPreferences.edit()) {
            putString(recentlyPlayedKey, Json.encodeToString(tracks))
            apply()
        }
    }

    fun clear() {
        _recentlyPlayed.update { emptyList() }
        with(sharedPreferences.edit()) {
            remove(recentlyPlayedKey)
            apply()
        }
    }
}


