package com.dn0ne.player.app.domain.online

/**
 * Sealed interface for unified online search results from multiple sources
 */
sealed interface OnlineSearchResult {
    data class YouTubeResult(val video: YouTubeVideo) : OnlineSearchResult
    data class SpotifyResult(val track: SpotifyTrack) : OnlineSearchResult
}



