package com.dn0ne.player.app.domain.online

import kotlinx.serialization.Serializable

/**
 * Data class representing a YouTube video search result
 */
@Serializable
data class YouTubeVideo(
    val videoId: String,
    val title: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val channelTitle: String? = null,
    val publishedAt: String? = null,
    val duration: String? = null,
    val viewCount: Long? = null
)



