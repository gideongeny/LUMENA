package com.dn0ne.player.app.data.online

import android.content.Context
import android.content.SharedPreferences
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Manages Spotify OAuth 2.0 authentication flow
 * Handles token storage and refresh securely
 */
class SpotifyAuthManager(
    private val context: Context,
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUri: String = "lumena://callback"
) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)
    
    private val accessTokenKey = "spotify_access_token"
    private val refreshTokenKey = "spotify_refresh_token"
    private val expiresAtKey = "spotify_expires_at"
    
    /**
     * Get the current access token, refreshing if necessary
     */
    suspend fun getAccessToken(): String? {
        val token = sharedPreferences.getString(accessTokenKey, null)
        val expiresAt = sharedPreferences.getLong(expiresAtKey, 0)
        
        // Check if token is expired or will expire soon (within 5 minutes)
        if (token != null && System.currentTimeMillis() < expiresAt - 300000) {
            return token
        }
        
        // Try to refresh the token
        val refreshToken = sharedPreferences.getString(refreshTokenKey, null)
        if (refreshToken != null) {
            return refreshAccessToken(refreshToken)
        }
        
        return null
    }
    
    /**
     * Check if user is authenticated (non-suspend version that checks token existence)
     */
    fun isAuthenticated(): Boolean {
        val token = sharedPreferences.getString(accessTokenKey, null)
        val expiresAt = sharedPreferences.getLong(expiresAtKey, 0)
        return token != null && System.currentTimeMillis() < expiresAt
    }
    
    /**
     * Start the OAuth authentication flow
     * Returns an AuthorizationRequest that should be opened in a browser/WebView
     */
    fun createAuthorizationRequest(): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
            .setScopes(arrayOf(
                "streaming",
                "user-read-email",
                "user-read-private",
                "user-read-playback-state",
                "user-modify-playback-state"
            ))
            .setCustomParam("show_dialog", "true")
            .build()
    }
    
    /**
     * Handle the authorization response and save tokens
     */
    fun handleAuthorizationResponse(response: AuthorizationResponse) {
        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                val accessToken = response.accessToken
                val expiresIn = response.expiresIn
                
                if (accessToken != null && expiresIn > 0) {
                    saveTokens(
                        accessToken = accessToken,
                        refreshToken = null, // Token flow doesn't provide refresh token
                        expiresIn = expiresIn
                    )
                }
            }
            AuthorizationResponse.Type.ERROR -> {
                // Handle error
                clearTokens()
            }
            else -> {
                // Unknown response type
            }
        }
    }
    
    /**
     * Refresh the access token using the refresh token
     */
    private suspend fun refreshAccessToken(refreshToken: String): String? {
        // Note: Spotify Web API token refresh requires server-side implementation
        // For now, this is a placeholder. In production, you'd need to:
        // 1. Make a POST request to https://accounts.spotify.com/api/token
        // 2. Include client_id, client_secret, grant_type=refresh_token, and refresh_token
        // 3. Update tokens with the new access token
        
        // For client-side only apps, we'll need to re-authenticate
        return null
    }
    
    /**
     * Save tokens securely
     */
    private fun saveTokens(accessToken: String, refreshToken: String?, expiresIn: Int) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000L)
        
        sharedPreferences.edit().apply {
            putString(accessTokenKey, accessToken)
            if (refreshToken != null) {
                putString(refreshTokenKey, refreshToken)
            }
            putLong(expiresAtKey, expiresAt)
            apply()
        }
    }
    
    /**
     * Clear all stored tokens
     */
    fun clearTokens() {
        sharedPreferences.edit().apply {
            remove(accessTokenKey)
            remove(refreshTokenKey)
            remove(expiresAtKey)
            apply()
        }
    }
    
    /**
     * Get access token synchronously (for use in non-suspend contexts)
     * Note: This doesn't handle token refresh
     */
    fun getAccessTokenSync(): String? {
        val token = sharedPreferences.getString(accessTokenKey, null)
        val expiresAt = sharedPreferences.getLong(expiresAtKey, 0)
        
        return if (token != null && System.currentTimeMillis() < expiresAt) {
            token
        } else {
            null
        }
    }
}

