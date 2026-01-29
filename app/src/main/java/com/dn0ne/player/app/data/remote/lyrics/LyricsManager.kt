package com.dn0ne.player.app.data.remote.lyrics

import android.content.Context
import android.util.Log
import com.dn0ne.player.app.data.repository.LyricsRepository
import com.dn0ne.player.app.domain.lyrics.Lyrics
import com.dn0ne.player.app.domain.result.DataError
import com.dn0ne.player.app.domain.result.Result
import com.dn0ne.player.app.domain.track.Track
import io.ktor.client.HttpClient

/**
 * Central manager for fetching lyrics from multiple sources with fallback chain
 * Implements caching using LyricsRepository
 */
class LyricsManager(
    private val context: Context,
    private val client: HttpClient,
    private val lyricsRepository: LyricsRepository
) {
    private val logTag = "LyricsManager"
    
    // Primary provider: LRCLIB
    private val lrclibProvider: LyricsProvider = LrclibLyricsProvider(context, client)
    
    // Fallback provider: YouTube Transcripts
    private val youtubeProvider: LyricsProvider = YouTubeTranscriptsProvider(context, client)
    
    /**
     * Get lyrics with fallback chain:
     * 1. Check cache
     * 2. Try LRCLIB
     * 3. Try YouTube Transcripts
     * 4. Cache successful result
     */
    suspend fun getLyrics(track: Track): Result<Lyrics, DataError.Network> {
        val trackUri = track.uri.toString()
        
        // Check cache first
        val cachedLyrics = lyricsRepository.getLyricsByUri(trackUri)
        if (cachedLyrics != null) {
            Log.d(logTag, "Returning cached lyrics for ${track.title}")
            return Result.Success(cachedLyrics)
        }
        
        // Try LRCLIB first (primary source)
        val lrclibResult = lrclibProvider.getLyrics(track)
        if (lrclibResult is Result.Success) {
            Log.d(logTag, "Found lyrics from LRCLIB for ${track.title}")
            // Add URI and cache the result
            val lyricsWithUri = lrclibResult.data.copy(uri = trackUri)
            lyricsRepository.insertLyrics(lyricsWithUri)
            return Result.Success(lyricsWithUri)
        }
        
        // Fallback to YouTube transcripts
        Log.d(logTag, "LRCLIB failed, trying YouTube transcripts for ${track.title}")
        val youtubeResult = youtubeProvider.getLyrics(track)
        if (youtubeResult is Result.Success) {
            Log.d(logTag, "Found lyrics from YouTube for ${track.title}")
            // Add URI and cache the result
            val lyricsWithUri = youtubeResult.data.copy(uri = trackUri)
            lyricsRepository.insertLyrics(lyricsWithUri)
            return Result.Success(lyricsWithUri)
        }
        
        // All sources failed
        Log.w(logTag, "All lyrics sources failed for ${track.title}")
        return youtubeResult // Return the last error
    }
    
    /**
     * Get lyrics specifically from LRCLIB
     */
    suspend fun getLyricsFromLrclib(track: Track): Result<Lyrics, DataError.Network> {
        val trackUri = track.uri.toString()
        val result = lrclibProvider.getLyrics(track)
        if (result is Result.Success) {
            val lyricsWithUri = result.data.copy(uri = trackUri)
            lyricsRepository.insertLyrics(lyricsWithUri)
            return Result.Success(lyricsWithUri)
        }
        return result
    }
    
    /**
     * Get lyrics specifically from YouTube Transcripts
     */
    suspend fun getLyricsFromYouTube(track: Track): Result<Lyrics, DataError.Network> {
        val trackUri = track.uri.toString()
        val result = youtubeProvider.getLyrics(track)
        if (result is Result.Success) {
            val lyricsWithUri = result.data.copy(uri = trackUri)
            lyricsRepository.insertLyrics(lyricsWithUri)
            return Result.Success(lyricsWithUri)
        }
        return result
    }
    
    /**
     * Post lyrics to LRCLIB (only provider that supports posting)
     */
    suspend fun postLyrics(track: Track, lyrics: Lyrics): Result<Unit, DataError.Network> {
        return lrclibProvider.postLyrics(track, lyrics)
    }
}

