package com.dn0ne.player.app.data

import android.content.Context
import com.dn0ne.player.app.domain.track.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PlayStatsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("play-stats", Context.MODE_PRIVATE)
    private val playCountKey = "play-count"
    private val lastPlayedKey = "last-played"

    private val _playCounts = MutableStateFlow(loadIntMap(playCountKey))
    val playCounts = _playCounts.asStateFlow()

    private val _lastPlayed = MutableStateFlow(loadLongMap(lastPlayedKey))
    val lastPlayed = _lastPlayed.asStateFlow()

    fun getPlayCount(track: Track): Int = _playCounts.value[track.uri.toString()] ?: 0
    fun getLastPlayed(track: Track): Long = _lastPlayed.value[track.uri.toString()] ?: 0L

    fun onTrackPlayed(track: Track) {
        val uri = track.uri.toString()
        val now = System.currentTimeMillis()

        _playCounts.update { current ->
            val updated = current.toMutableMap().apply {
                put(uri, (current[uri] ?: 0) + 1)
            }
            saveIntMap(playCountKey, updated)
            updated
        }

        _lastPlayed.update { current ->
            val updated = current.toMutableMap().apply {
                put(uri, now)
            }
            saveLongMap(lastPlayedKey, updated)
            updated
        }
    }

    private fun loadIntMap(key: String): Map<String, Int> {
        val json = sharedPreferences.getString(key, null) ?: return emptyMap()
        return try {
            Json.decodeFromString<Map<String, Int>>(json)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun loadLongMap(key: String): Map<String, Long> {
        val json = sharedPreferences.getString(key, null) ?: return emptyMap()
        return try {
            Json.decodeFromString<Map<String, Long>>(json)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun saveIntMap(key: String, map: Map<String, Int>) {
        with(sharedPreferences.edit()) {
            putString(key, Json.encodeToString(map))
            apply()
        }
    }

    private fun saveLongMap(key: String, map: Map<String, Long>) {
        with(sharedPreferences.edit()) {
            putString(key, Json.encodeToString(map))
            apply()
        }
    }
}

