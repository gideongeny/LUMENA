package com.dn0ne.player.app.data.online

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

/**
 * Repository for fetching YouTube dislike counts from Return YouTube Dislike API
 * API: https://returnyoutubedislikeapi.com/
 */
class ReturnYouTubeDislikeRepository(
    private val client: HttpClient
) {
    private val apiBaseUrl = "https://returnyoutubedislikeapi.com"
    
    /**
     * Get video statistics including dislike count
     */
    suspend fun getVideoStats(videoId: String): kotlin.Result<VideoStats> {
        return try {
            val response = client.get("$apiBaseUrl/Votes") {
                url {
                    parameters.append("videoId", videoId)
                }
            }
            
            val stats: VideoStatsResponse = response.body()
            Result.success(
                VideoStats(
                    likes = stats.likes,
                    dislikes = stats.dislikes,
                    viewCount = stats.viewCount,
                    rating = stats.rating
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class VideoStats(
    val likes: Long?,
    val dislikes: Long?,
    val viewCount: Long?,
    val rating: Double?
)

@Serializable
private data class VideoStatsResponse(
    val likes: Long? = null,
    val dislikes: Long? = null,
    val viewCount: Long? = null,
    val rating: Double? = null
)

