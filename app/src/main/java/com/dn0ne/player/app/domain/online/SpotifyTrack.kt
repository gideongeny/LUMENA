package com.dn0ne.player.app.domain.online

import kotlinx.serialization.Serializable

/**
 * Data class representing a Spotify track search result
 */
@Serializable
data class SpotifyTrack(
    val id: String,
    val name: String,
    val artists: List<SpotifyArtist> = emptyList(),
    val album: SpotifyAlbum? = null,
    val durationMs: Long? = null,
    val previewUrl: String? = null,
    val externalUrls: Map<String, String> = emptyMap()
)

@Serializable
data class SpotifyArtist(
    val id: String,
    val name: String
)

@Serializable
data class SpotifyAlbum(
    val id: String,
    val name: String,
    val images: List<SpotifyImage> = emptyList()
)

@Serializable
data class SpotifyImage(
    val url: String,
    val width: Int? = null,
    val height: Int? = null
)



