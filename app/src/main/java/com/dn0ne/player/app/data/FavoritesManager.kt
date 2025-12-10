package com.dn0ne.player.app.data

import android.content.Context
import com.dn0ne.player.app.domain.track.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FavoritesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val favoritesKey = "favorites"

    private val _favorites = MutableStateFlow<Set<String>>(loadFavorites())
    val favorites = _favorites.asStateFlow()

    private fun loadFavorites(): Set<String> {
        val json = sharedPreferences.getString(favoritesKey, null) ?: return emptySet()
        return try {
            Json.decodeFromString<Set<String>>(json)
        } catch (e: Exception) {
            emptySet()
        }
    }

    fun isFavorite(track: Track): Boolean {
        return _favorites.value.contains(track.uri.toString())
    }

    fun toggleFavorite(track: Track) {
        _favorites.update { currentSet ->
            val updated = if (currentSet.contains(track.uri.toString())) {
                currentSet - track.uri.toString()
            } else {
                currentSet + track.uri.toString()
            }
            saveFavorites(updated)
            updated
        }
    }

    fun getFavoriteTracks(allTracks: List<Track>): List<Track> {
        val favoriteUris = _favorites.value
        return allTracks.filter { favoriteUris.contains(it.uri.toString()) }
    }

    private fun saveFavorites(favorites: Set<String>) {
        with(sharedPreferences.edit()) {
            putString(favoritesKey, Json.encodeToString(favorites))
            apply()
        }
    }

    fun clear() {
        _favorites.update { emptySet() }
        with(sharedPreferences.edit()) {
            remove(favoritesKey)
            apply()
        }
    }
}



