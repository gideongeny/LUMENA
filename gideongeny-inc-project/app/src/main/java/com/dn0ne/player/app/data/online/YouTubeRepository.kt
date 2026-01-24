package com.dn0ne.player.app.data.online

import com.dn0ne.player.app.domain.track.Track
import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.services.youtube.YoutubeService
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

class YouTubeRepository(private val context: android.content.Context) {
    companion object {
        fun extractVideoId(url: String): String {
            return when {
                url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
                url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?").substringBefore("&")
                else -> url.substringAfterLast("/").substringBefore("?").substringBefore("&")
            }
        }
    }

    init {
        try {
            // Initialize NewPipe if not already initialized
            if (NewPipe.getDownloader() == null) {
                NewPipe.init(LumenaDownloader())
                // Configure for maximum content availability
                NewPipe.setPreferredContentCountry(ContentCountry("US"))
                NewPipe.setPreferredLocalization(Localization("en", "US"))
                android.util.Log.d("YouTubeRepository", "NewPipe initialized with US locale for maximum content")
            } else {
                android.util.Log.d("YouTubeRepository", "NewPipe already initialized")
            }
            
            // Initialize YoutubeDL
            try {
                com.yausername.youtubedl_android.YoutubeDL.getInstance().init(context)
                android.util.Log.d("YouTubeRepository", "YoutubeDL initialized")
            } catch (e: Exception) {
                android.util.Log.e("YouTubeRepository", "Failed to initialize YoutubeDL", e)
            }
            
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "Failed to initialize NewPipe", e)
            e.printStackTrace()
        }
    }

    suspend fun search(query: String): Result<List<Track>> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("YouTubeRepository", "Starting search for: $query")
            
            // Try NewPipe first
            val newPipeResults = searchWithNewPipe(query)
            if (newPipeResults.isNotEmpty()) {
                android.util.Log.d("YouTubeRepository", "NewPipe search successful: ${newPipeResults.size} results")
                return@withContext Result.success(newPipeResults)
            }
            
            // Fallback to YouTube InnerTube API (public, no key required)
            android.util.Log.d("YouTubeRepository", "NewPipe returned no results, trying InnerTube API")
            val innerTubeResults = searchWithInnerTubeAPI(query)
            if (innerTubeResults.isNotEmpty()) {
                android.util.Log.d("YouTubeRepository", "InnerTube API search successful: ${innerTubeResults.size} results")
                return@withContext Result.success(innerTubeResults)
            }
            
            android.util.Log.w("YouTubeRepository", "All search methods returned no results")
            Result.success(emptyList())
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "Search failed for query: $query", e)
            Result.failure(e)
        }
    }

    private suspend fun searchWithNewPipe(query: String): List<Track> {
        return try {
            val service = ServiceList.YouTube
            val searchExtractor = service.getSearchExtractor(
                query,
                listOf("videos"),
                null
            )
            searchExtractor.fetchPage()
            
            searchExtractor.initialPage.items.mapNotNull { item ->
                if (item is org.schabi.newpipe.extractor.stream.StreamInfoItem) {
                    try {
                        val videoId = extractVideoId(item.url)
                        val streamUrl = "https://www.youtube.com/watch?v=$videoId"
                        val uri = android.net.Uri.parse(streamUrl)
                        
                        // Normalize thumbnail URL
                        val thumbnailUrl = item.thumbnails?.firstOrNull()?.url ?: ""
                        val normalizedThumbnail = if (thumbnailUrl.startsWith("//")) {
                            "https:$thumbnailUrl"
                        } else if (thumbnailUrl.isNotEmpty() && !thumbnailUrl.startsWith("http")) {
                            "https://$thumbnailUrl"
                        } else {
                            thumbnailUrl
                        }
                        
                        Track(
                            uri = uri,
                            mediaItem = androidx.media3.common.MediaItem.fromUri(uri),
                            coverArtUri = android.net.Uri.parse(normalizedThumbnail),
                            duration = (item.duration * 1000).toInt(), // Duration in ms
                            size = 0,
                            dateModified = System.currentTimeMillis(),
                            data = streamUrl,
                            title = item.name,
                            artist = item.uploaderName ?: "Unknown Artist",
                            album = "YouTube",
                            trackNumber = "0",
                            year = "2026"
                        )
                    } catch (e: Exception) {
                        android.util.Log.w("YouTubeRepository", "Failed to parse item: ${item.name}", e)
                        null
                    }
                } else null
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "NewPipe search failed", e)
            emptyList()
        }
    }

    private suspend fun searchWithInnerTubeAPI(query: String): List<Track> {
        return try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val apiUrl = "https://www.youtube.com/youtubei/v1/search?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
            
            val requestBody = """
                {
                    "context": {
                        "client": {
                            "clientName": "WEB",
                            "clientVersion": "2.20240101.00.00"
                        }
                    },
                    "query": "$query"
                }
            """.trimIndent()
            
            val connection = URL(apiUrl).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            connection.outputStream.use { it.write(requestBody.toByteArray()) }
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            parseInnerTubeResponse(response)
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube API search failed", e)
            emptyList()
        }
    }

    private fun parseInnerTubeResponse(response: String): List<Track> {
        return try {
            val json = JSONObject(response)
            val contents = json.optJSONObject("contents")
                ?.optJSONObject("twoColumnSearchResultsRenderer")
                ?.optJSONObject("primaryContents")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents")
                ?.optJSONObject(0)
                ?.optJSONObject("itemSectionRenderer")
                ?.optJSONArray("contents")
            
            val tracks = mutableListOf<Track>()
            if (contents != null) {
                for (i in 0 until contents.length()) {
                    val item = contents.optJSONObject(i)?.optJSONObject("videoRenderer")
                    if (item != null) {
                        try {
                            val videoId = extractVideoId("https://www.youtube.com/watch?v=" + item.optString("videoId")) // Ensure it's treated as a URL or just use videoId
                            val title = item.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: "Unknown"
                            val artist = item.optJSONObject("ownerText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: "Unknown Artist"
                            val thumbnail = item.optJSONObject("thumbnail")?.optJSONArray("thumbnails")?.optJSONObject(0)?.optString("url") ?: ""
                            val durationText = item.optJSONObject("lengthText")?.optString("simpleText") ?: "0:00"
                            val duration = parseDuration(durationText)
                            
                            // Normalize thumbnail URL
                            val normalizedThumbnail = if (thumbnail.startsWith("//")) {
                                "https:$thumbnail"
                            } else if (thumbnail.isNotEmpty() && !thumbnail.startsWith("http")) {
                                "https://$thumbnail"
                            } else {
                                thumbnail
                            }
                            
                            val streamUrl = "https://www.youtube.com/watch?v=$videoId"
                            val uri = android.net.Uri.parse(streamUrl)
                            
                            tracks.add(Track(
                                uri = uri,
                                mediaItem = MediaItem.fromUri(uri),
                                coverArtUri = android.net.Uri.parse(normalizedThumbnail),
                                duration = duration,
                                size = 0,
                                dateModified = System.currentTimeMillis(),
                                data = streamUrl,
                                title = title,
                                artist = artist,
                                album = "YouTube",
                                trackNumber = "0",
                                year = "2026"
                            ))
                        } catch (e: Exception) {
                            android.util.Log.w("YouTubeRepository", "Failed to parse video item", e)
                        }
                    }
                }
            }
            tracks
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "Failed to parse InnerTube response", e)
            emptyList()
        }
    }

    private fun parseDuration(durationText: String): Int {
        return try {
            val parts = durationText.split(":")
            when (parts.size) {
                2 -> (parts[0].toInt() * 60 + parts[1].toInt()) * 1000 // MM:SS
                3 -> (parts[0].toInt() * 3600 + parts[1].toInt() * 60 + parts[2].toInt()) * 1000 // HH:MM:SS
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getStreamUrl(videoId: String): Result<String> = withContext<Result<String>>(Dispatchers.IO) {
        android.util.Log.d("YouTubeRepository", "Starting stream extraction for video: $videoId")
        
        // 1. Try InnerTube API (ANDROID_MUSIC) - Most reliable for music tracks
        try {
            android.util.Log.d("YouTubeRepository", "Trying InnerTube API (ANDROID_MUSIC) as primary method")
            val url = getStreamUrlWithInnerTube(videoId, "ANDROID_MUSIC")
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "ANDROID_MUSIC extraction successful: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube ANDROID_MUSIC failed: ${e.message}", e)
        }

        // 2. Try InnerTube API (TVHTML5SIMPLY_EMBEDDED) - very reliable for non-ciphered high quality streams
        try {
            android.util.Log.d("YouTubeRepository", "Trying InnerTube API (TVHTML5SIMPLY_EMBEDDED)")
            val url = getStreamUrlWithInnerTube(videoId, "TVHTML5SIMPLY_EMBEDDED")
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "TVHTML5SIMPLY_EMBEDDED success: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube TVHTML5SIMPLY_EMBEDDED failed", e)
        }

        // 3. Try InnerTube API (IOS Client) - good for HLS
        try {
            android.util.Log.d("YouTubeRepository", "Trying InnerTube API (IOS)")
            val url = getStreamUrlWithInnerTube(videoId, "IOS")
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "IOS extraction successful: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube IOS failed: ${e.message}", e)
        }

        // 4. Try Android Test Suite
        try {
            android.util.Log.d("YouTubeRepository", "Trying InnerTube API (ANDROID_TESTSUITE)")
            val url = getStreamUrlWithInnerTube(videoId, "ANDROID_TESTSUITE")
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "ANDROID_TESTSUITE extraction successful: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube ANDROID_TESTSUITE failed: ${e.message}", e)
        }

        // 5. Try InnerTube API (ANDROID)
        try {
            android.util.Log.d("YouTubeRepository", "Trying InnerTube API (ANDROID)")
            val url = getStreamUrlWithInnerTube(videoId, "ANDROID")
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "ANDROID success: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube ANDROID failed", e)
        }

        // Try WEB client
        try {
            android.util.Log.d("YouTubeRepository", "Trying InnerTube API (WEB)")
            val url = getStreamUrlWithInnerTube(videoId, "WEB")
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "WEB extraction successful: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube WEB failed: ${e.message}", e)
        }

        // Try WEB_CREATOR client
        try {
            android.util.Log.d("YouTubeRepository", "Trying InnerTube API (WEB_CREATOR)")
            val url = getStreamUrlWithInnerTube(videoId, "WEB_CREATOR")
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "WEB_CREATOR extraction successful: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube WEB_CREATOR failed: ${e.message}", e)
        }

        // Try MWEB client
        try {
            android.util.Log.d("YouTubeRepository", "Trying InnerTube API (MWEB)")
            val url = getStreamUrlWithInnerTube(videoId, "MWEB")
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "MWEB extraction successful: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "InnerTube MWEB failed: ${e.message}", e)
        }

        // Last resort: Try NewPipe
        try {
            android.util.Log.d("YouTubeRepository", "Trying NewPipe as last resort")
            val service = ServiceList.YouTube
            val streamExtractor = service.getStreamExtractor("https://www.youtube.com/watch?v=$videoId")
            streamExtractor.fetchPage()
            
            val audioStreams = streamExtractor.audioStreams
            val bestStream = audioStreams.maxByOrNull { it.bitrate }
            
            if (bestStream != null) {
                val url = bestStream.url ?: throw Exception("Stream URL is null")
                android.util.Log.d("YouTubeRepository", "NewPipe extraction successful: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "NewPipe extraction failed: ${e.message}", e)
        }

        // True last resort: Try YoutubeDL if available
        try {
            android.util.Log.d("YouTubeRepository", "Trying YoutubeDL as true last resort")
            val url = getStreamUrlWithYoutubeDL(videoId)
            if (url != null) {
                android.util.Log.d("YouTubeRepository", "YoutubeDL extraction successful: $url")
                return@withContext Result.success(url)
            }
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "YoutubeDL extraction failed: ${e.message}", e)
        }

        android.util.Log.e("YouTubeRepository", "All extraction methods failed for video: $videoId")
        Result.failure(Exception("All extraction methods failed. Video may be restricted or unavailable."))
    }

    private suspend fun getStreamUrlWithYoutubeDL(videoId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://www.youtube.com/watch?v=$videoId"
                val request = com.yausername.youtubedl_android.YoutubeDLRequest(url)
                request.addOption("-f", "bestaudio")
                val streamInfo = com.yausername.youtubedl_android.YoutubeDL.getInstance().getInfo(request)
                streamInfo.url
            } catch (e: Exception) {
                android.util.Log.e("YouTubeRepository", "YoutubeDL extraction failed for $videoId", e)
                null
            }
        }
    }

    private fun getStreamUrlWithInnerTube(videoId: String, clientType: String): String? {
        try {
            val apiUrl = "https://www.youtube.com/youtubei/v1/player?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
            val clientContext = when(clientType) {
                "ANDROID_MUSIC" -> """
                        "clientName": "ANDROID_MUSIC",
                        "clientVersion": "6.45.54",
                        "androidSdkVersion": 31,
                        "osName": "Android",
                        "osVersion": "12",
                        "platform": "MOBILE"
                """.trimIndent()
                "IOS" -> """
                        "clientName": "IOS",
                        "clientVersion": "19.29.1",
                        "deviceMake": "Apple",
                        "deviceModel": "iPhone14,5",
                        "userAgent": "com.google.ios.youtube/19.29.1 (iPhone14,5; U; CPU iOS 17_5_1 like Mac OS X;)",
                        "osName": "iOS",
                        "osVersion": "17.5.1"
                """.trimIndent()
                "ANDROID_TESTSUITE" -> """
                        "clientName": "ANDROID_TESTSUITE",
                        "clientVersion": "1.95",
                        "androidSdkVersion": 30
                """.trimIndent()
                "WEB" -> """
                        "clientName": "WEB",
                        "clientVersion": "2.20240101.00.00"
                """.trimIndent()
                "MWEB" -> """
                        "clientName": "MWEB",
                        "clientVersion": "2.20231201.01.00"
                """.trimIndent()
                "ANDROID_VR" -> """
                        "clientName": "ANDROID_VR",
                        "clientVersion": "1.50.60",
                        "androidSdkVersion": 30
                """.trimIndent()
                "ANDROID" -> """
                        "clientName": "ANDROID",
                        "clientVersion": "19.00.00",
                        "androidSdkVersion": 33
                """.trimIndent()
                "TVHTML5SIMPLY_EMBEDDED" -> """
                        "clientName": "TVHTML5SIMPLY_EMBEDDED",
                        "clientVersion": "2.20231201"
                """.trimIndent()
                "WEB_CREATOR" -> """
                        "clientName": "WEB_CREATOR",
                        "clientVersion": "1.20231201.01.00"
                """.trimIndent()
                 else -> ""
            }

            val requestBody = """
                {
                    "videoId": "$videoId",
                    "context": {
                        "client": {
                            $clientContext
                        }
                    }
                }
            """.trimIndent()
            
            val connection = URL(apiUrl).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("User-Agent", LumenaDownloader.USER_AGENT)
            connection.setRequestProperty("Origin", "https://www.youtube.com")
            connection.setRequestProperty("Referer", "https://www.youtube.com/")
            connection.setRequestProperty("X-Goog-Api-Format-Version", "2")
            connection.doOutput = true
            
            connection.outputStream.use { it.write(requestBody.toByteArray()) }
            
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            
            val json = JSONObject(response)
            
            // Check for playability errors and log them
            val playabilityStatus = json.optJSONObject("playabilityStatus")
            if (playabilityStatus != null) {
                val status = playabilityStatus.optString("status")
                if (status != "OK") {
                    val reason = playabilityStatus.optString("reason")
                    android.util.Log.e("YouTubeRepository", "InnerTube extraction failed ($clientType): $status, Reason: $reason")
                    return null
                }
            }

            val streamingData = json.optJSONObject("streamingData") ?: return null
            
            // For IOS/WEB/ANDROID, check HLS
            val hlsManifestUrl = streamingData.optString("hlsManifestUrl")
            if (hlsManifestUrl.isNotEmpty()) {
                 android.util.Log.d("YouTubeRepository", "Found HLS manifest: $hlsManifestUrl")
                 return hlsManifestUrl
            }
            
            // Check DASH
            val dashManifestUrl = streamingData.optString("dashManifestUrl")
            if (dashManifestUrl.isNotEmpty()) {
                 android.util.Log.d("YouTubeRepository", "Found DASH manifest: $dashManifestUrl")
                 return dashManifestUrl
            }

            // Try regular formats
            val formats = streamingData.optJSONArray("formats")
            if (formats != null) {
                for (i in 0 until formats.length()) {
                    val format = formats.optJSONObject(i)
                    val url = format?.optString("url")
                    if (!url.isNullOrEmpty()) {
                        return url
                    }
                    // Handle cipher
                    val cipher = format?.optString("signatureCipher") ?: format?.optString("cipher")
                    if (!cipher.isNullOrEmpty()) {
                        // We can't easily decipher here without JS engine, but we can try other formats
                        continue
                    }
                }
            }

            // Try adaptive formats (prefer audio)
            val adaptiveFormats = streamingData.optJSONArray("adaptiveFormats")
            if (adaptiveFormats != null) {
                val audioFormats = mutableListOf<JSONObject>()
                for (i in 0 until adaptiveFormats.length()) {
                    val format = adaptiveFormats.optJSONObject(i)
                    if (format?.optString("mimeType")?.contains("audio") == true) {
                        audioFormats.add(format)
                    }
                }
                
                // Sort by bitrate descending
                audioFormats.sortByDescending { it.optInt("bitrate") }
                
                for (format in audioFormats) {
                    val url = format.optString("url")
                    if (url.isNotEmpty()) return url
                    
                    // Note: If no URL is present, it's likely a ciphered format which we currently don't support in InnerTube fallback
                }
            }
            
            return null
        } catch (e: Exception) {
            android.util.Log.e("YouTubeRepository", "Error in getStreamUrlWithInnerTube($clientType)", e)
            return null
        }
    }
}
