package com.dn0ne.player.app.data.online

import com.dn0ne.player.app.domain.track.Track
import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.services.youtube.YoutubeService
import org.schabi.newpipe.extractor.stream.StreamInfo

class YouTubeRepository {

    init {
        // Initialize NewPipe if not already initialized
        if (NewPipe.getDownloader() == null) {
            NewPipe.init(LumenaDownloader())
        }
    }

    suspend fun search(query: String): Result<List<Track>> = withContext(Dispatchers.IO) {
        try {
            val service = ServiceList.YouTube
            val searchExtractor = service.getSearchExtractor(
                query,
                listOf("videos"),
                null
            )
            searchExtractor.fetchPage()
            
            val results = searchExtractor.initialPage.items.mapNotNull { item ->
                if (item is org.schabi.newpipe.extractor.stream.StreamInfoItem) {
                    val streamUrl = "https://www.youtube.com/watch?v=${item.url.substringAfter("v=")}"
                    val uri = android.net.Uri.parse(streamUrl)
                    Track(
                        uri = uri,
                        mediaItem = androidx.media3.common.MediaItem.fromUri(uri),
                        coverArtUri = android.net.Uri.parse(item.thumbnails?.firstOrNull()?.url ?: ""),
                        duration = item.duration.toInt() * 1000, // Duration in ms
                        size = 0,
                        dateModified = System.currentTimeMillis(),
                        data = streamUrl,
                        title = item.name,
                        artist = item.uploaderName,
                        album = "YouTube",
                        trackNumber = "0",
                        year = "2026"
                    )
                } else null
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStreamUrl(videoId: String): Result<String> = withContext<Result<String>>(Dispatchers.IO) {
        try {
            val service = ServiceList.YouTube
            val streamExtractor = service.getStreamExtractor("https://www.youtube.com/watch?v=$videoId")
            streamExtractor.fetchPage()
            
            // Get best audio stream
            val audioStreams = streamExtractor.audioStreams
            val bestStream = audioStreams.maxByOrNull { it.bitrate }
            
            if (bestStream != null) {
                // Explicitly return Result<String>
                val url: String = bestStream.url ?: throw Exception("Stream URL is null")
                Result.success(url)
            } else {
                Result.failure<String>(Exception("No audio stream found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
