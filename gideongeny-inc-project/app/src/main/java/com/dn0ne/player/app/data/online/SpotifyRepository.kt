package com.dn0ne.player.app.data.online

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dn0ne.player.BuildConfig
import com.dn0ne.player.app.data.online.SpotifyAuthManager
import com.dn0ne.player.app.domain.online.SpotifyTrack
// Temporarily commented out - Spotify App Remote dependency not available
// import com.spotify.android.appremote.api.ConnectionParams
// import com.spotify.android.appremote.api.Connector
// import com.spotify.android.appremote.api.SpotifyAppRemote
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Repository for Spotify Web API operations
 */
class SpotifyRepository(
    private val context: Context,
    private val client: HttpClient,
    private val authManager: SpotifyAuthManager
) {
    private val spotifyApiBaseUrl = "https://api.spotify.com/v1"
    // private var spotifyAppRemote: SpotifyAppRemote? = null // Temporarily commented out
    
    /**
     * Search for tracks on Spotify
     */
    suspend fun searchTracks(query: String, limit: Int = 20): Result<List<SpotifyTrack>> {
        val accessToken = authManager.getAccessToken()
        if (accessToken == null) {
            return Result.failure(Exception("Not authenticated"))
        }
        
        return try {
            val response = client.get("$spotifyApiBaseUrl/search") {
                url {
                    parameters.append("q", query)
                    parameters.append("type", "track")
                    parameters.append("limit", limit.toString())
                }
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            
            val searchResponse: SpotifySearchResponse = response.body()
            Result.success(searchResponse.tracks.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Connect to Spotify App Remote for playback control
     */
    // Temporarily commented out - Spotify App Remote not available
    // fun connectSpotifyAppRemote(
    //     onConnected: (SpotifyAppRemote) -> Unit,
    //     onFailure: (Throwable) -> Unit
    // ) {
    //     val connectionParams = ConnectionParams.Builder(BuildConfig.SPOTIFY_CLIENT_ID)
    //         .setRedirectUri("lumena://callback")
    //         .showAuthView(true)
    //         .build()
    //     
    //     SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
    //         override fun onConnected(appRemote: SpotifyAppRemote) {
    //             spotifyAppRemote = appRemote
    //             onConnected(appRemote)
    //         }
    //         
    //         override fun onFailure(throwable: Throwable) {
    //             onFailure(throwable)
    //         }
    //     })
    // }
    // 
    // /**
    //  * Play a track using Spotify App Remote
    //  */
    // suspend fun playTrack(uri: String) {
    //     spotifyAppRemote?.playerApi?.play(uri)
    // }
    
    /**
     * Open Spotify app with a track URI
     */
    fun openSpotifyTrack(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.spotify.music")
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Spotify app not installed, open in browser
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri.replace("spotify:track:", "https://open.spotify.com/track/")))
            context.startActivity(webIntent)
        }
    }
    
    /**
     * Disconnect from Spotify App Remote
     * Temporarily commented out - Spotify App Remote not available
     */
    // fun disconnect() {
    //     SpotifyAppRemote.disconnect(spotifyAppRemote)
    //     spotifyAppRemote = null
    // }
}

@Serializable
private data class SpotifySearchResponse(
    val tracks: SpotifyTracksResponse
)

@Serializable
private data class SpotifyTracksResponse(
    val items: List<SpotifyTrack>
)

