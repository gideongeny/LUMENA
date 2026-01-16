package com.dn0ne.player.app.data.online

import android.content.SharedPreferences

/**
 * Helper class to manage OpenAI API key storage and retrieval
 */
class OpenAIApiKeyManager(
    private val encryptedPrefs: SharedPreferences
) {
    private val apiKeyKey = "openai_api_key"
    
    /**
     * Get the stored OpenAI API key
     */
    fun getApiKey(): String? {
        return encryptedPrefs.getString(apiKeyKey, null)
    }
    
    /**
     * Save the OpenAI API key securely
     */
    fun saveApiKey(apiKey: String) {
        encryptedPrefs.edit()
            .putString(apiKeyKey, apiKey)
            .apply()
    }
    
    /**
     * Check if API key is set
     */
    fun hasApiKey(): Boolean {
        return getApiKey() != null && getApiKey()!!.isNotBlank()
    }
    
    /**
     * Clear the stored API key
     */
    fun clearApiKey() {
        encryptedPrefs.edit()
            .remove(apiKeyKey)
            .apply()
    }
}



