package com.dn0ne.player.app.data.online

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Repository for fetching SponsorBlock skip segments
 * API: https://sponsor.ajay.app/
 */
class SponsorBlockRepository(
    private val client: HttpClient
) {
    private val apiBaseUrl = "https://sponsor.ajay.app/api"
    
    /**
     * Get skip segments for a YouTube video
     */
    suspend fun getSkipSegments(videoId: String): kotlin.Result<List<SkipSegment>> {
        return try {
            val response = client.get("$apiBaseUrl/skipSegments") {
                url {
                    parameters.append("videoID", videoId)
                }
            }
            
            val segments: List<SkipSegmentResponse> = response.body()
            Result.success(
                segments.flatMap { it.segments.map { dto -> dto.toSkipSegment() } }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
data class SkipSegment(
    val startTime: Double,
    val endTime: Double,
    val category: String
)

@Serializable
private data class SkipSegmentResponse(
    @SerialName("videoID")
    val videoId: String,
    val segments: List<SkipSegmentDto>
)

@Serializable
private data class SkipSegmentDto(
    val segment: List<Double>, // [startTime, endTime]
    val category: String
) {
    fun toSkipSegment(): SkipSegment {
        return SkipSegment(
            startTime = segment[0],
            endTime = segment[1],
            category = category
        )
    }
}

