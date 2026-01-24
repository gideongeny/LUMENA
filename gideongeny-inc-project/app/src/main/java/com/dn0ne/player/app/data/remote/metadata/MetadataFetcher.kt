package com.dn0ne.player.app.data.remote.metadata

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MetadataFetcher(private val client: HttpClient) {
    
    suspend fun fetchAlbumArtUrl(artist: String?, album: String?): String? {
        // Handle blank or unknown values
        val cleanArtist = artist?.takeIf { 
            it.isNotBlank() && !it.equals("unknown", ignoreCase = true) 
        } ?: return null
        
        val cleanAlbum = album?.takeIf { 
            it.isNotBlank() && !it.equals("unknown", ignoreCase = true) 
        } ?: return null
        
        return try {
            val response = client.get("https://www.theaudiodb.com/api/v1/json/2/searchalbum.php") {
                url {
                    parameters.append("s", cleanArtist)
                    parameters.append("a", cleanAlbum)
                }
            }
            
            val audioDbResponse: AudioDbResponse = response.body()
            audioDbResponse.album?.firstOrNull()?.strAlbumThumb
        } catch (e: Exception) {
            null
        }
    }
}

@Serializable
private data class AudioDbResponse(
    val album: List<AlbumInfo>? = null
)

@Serializable
private data class AlbumInfo(
    @SerialName("strAlbumThumb")
    val strAlbumThumb: String? = null
)

