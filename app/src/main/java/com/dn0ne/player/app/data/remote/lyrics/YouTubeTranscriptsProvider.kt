package com.dn0ne.player.app.data.remote.lyrics

import android.content.Context
import android.util.Log
import com.dn0ne.player.BuildConfig
import com.dn0ne.player.app.domain.lyrics.Lyrics
import com.dn0ne.player.app.domain.result.DataError
import com.dn0ne.player.app.domain.result.Result
import com.dn0ne.player.app.domain.track.Track
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.CaptionListResponse
import com.google.api.services.youtube.model.CaptionSnippet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Provider for fetching lyrics from YouTube video captions/transcripts
 */
class YouTubeTranscriptsProvider(
    private val context: Context,
    private val client: HttpClient
) : LyricsProvider {
    
    private val youtube: YouTube by lazy {
        YouTube.Builder(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            null
        )
            .setApplicationName("Lumena")
            .build()
    }
    
    private val logTag = "YouTubeTranscriptsProvider"
    
    /**
     * Get lyrics from YouTube video captions
     * First searches for a video matching the track, then fetches captions
     */
    override suspend fun getLyrics(track: Track): Result<Lyrics, DataError.Network> {
        if (track.title == null || track.artist == null) {
            return Result.Error(DataError.Network.BadRequest)
        }
        
        return withContext(Dispatchers.IO) {
            try {
                // Search for video
                val searchQuery = "${track.artist} ${track.title}"
                val searchRequest = youtube.search()
                    .list(listOf("snippet"))
                    .setKey(BuildConfig.YOUTUBE_API_KEY)
                    .setQ(searchQuery)
                    .setType(listOf("video"))
                    .setMaxResults(1L)
                
                val searchResponse = searchRequest.execute()
                val videoId = searchResponse.items?.firstOrNull()?.id?.videoId
                    ?: return@withContext Result.Error(DataError.Network.NotFound)
                
                // Get caption tracks
                val captionRequest = youtube.captions()
                    .list(listOf("snippet"), videoId)
                    .setKey(BuildConfig.YOUTUBE_API_KEY)
                
                val captionResponse: CaptionListResponse = captionRequest.execute()
                val captionTrack = captionResponse.items?.firstOrNull { caption ->
                    caption.snippet?.language == "en" || 
                    caption.snippet?.language?.startsWith("en") == true
                } ?: captionResponse.items?.firstOrNull()
                
                if (captionTrack == null) {
                    return@withContext Result.Error(DataError.Network.NotFound)
                }
                
                // Download caption content
                val captionId = captionTrack.id
                val downloadRequest = youtube.captions()
                    .download(captionId)
                    .setKey(BuildConfig.YOUTUBE_API_KEY)
                    .setTfmt("srt") // SubRip format
                
                val captionContent = downloadRequest.executeMediaAsInputStream()
                    .bufferedReader().use { it.readText() }
                
                // Parse SRT format to timed lyrics
                val parsedLyrics = parseSrtToTimedLyrics(captionContent)
                
                Result.Success(
                    Lyrics(
                        uri = track.uri.toString(), // Add URI for caching
                        plain = parsedLyrics.map { it.second },
                        synced = parsedLyrics.map { it.first to it.second }
                    )
                )
            } catch (e: Exception) {
                Log.e(logTag, "Error fetching YouTube transcripts", e)
                Result.Error(DataError.Network.Unknown)
            }
        }
    }
    
    /**
     * Parse SRT (SubRip) format to timed lyrics
     */
    private fun parseSrtToTimedLyrics(srtContent: String): List<Pair<Int, String>> {
        val lines = srtContent.lines()
        val timedLyrics = mutableListOf<Pair<Int, String>>()
        var i = 0
        
        while (i < lines.size) {
            // Skip sequence number
            if (lines[i].trim().isEmpty() || lines[i].trim().toIntOrNull() != null) {
                i++
                continue
            }
            
            // Parse timestamp (format: 00:00:00,000 --> 00:00:01,000)
            if (i < lines.size - 1 && lines[i].contains("-->")) {
                val timestampLine = lines[i]
                val startTime = parseSrtTimestamp(timestampLine.substringBefore(" -->"))
                i++
                
                // Get subtitle text (may span multiple lines)
                val textLines = mutableListOf<String>()
                while (i < lines.size && lines[i].trim().isNotEmpty() && !lines[i].contains("-->")) {
                    textLines.add(lines[i].trim())
                    i++
                }
                
                val text = textLines.joinToString(" ").trim()
                if (text.isNotEmpty()) {
                    timedLyrics.add(startTime to text)
                }
            } else {
                i++
            }
        }
        
        return timedLyrics
    }
    
    /**
     * Parse SRT timestamp to milliseconds
     */
    private fun parseSrtTimestamp(timestamp: String): Int {
        val parts = timestamp.trim().split(":", ",")
        if (parts.size != 4) return 0
        
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts[1].toIntOrNull() ?: 0
        val seconds = parts[2].toIntOrNull() ?: 0
        val milliseconds = parts[3].toIntOrNull() ?: 0
        
        return (hours * 3600 + minutes * 60 + seconds) * 1000 + milliseconds
    }
    
    /**
     * Post lyrics is not supported for YouTube transcripts
     */
    override suspend fun postLyrics(track: Track, lyrics: Lyrics): Result<Unit, DataError.Network> {
        return Result.Error(DataError.Network.BadRequest)
    }
}

