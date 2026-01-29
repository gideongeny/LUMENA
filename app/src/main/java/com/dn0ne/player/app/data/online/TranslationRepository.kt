package com.dn0ne.player.app.data.online

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

/**
 * Repository for AI-powered translation using Gemini
 */
class TranslationRepository(
    private val apiKey: String,
    private val client: HttpClient
) {
    
    /**
     * Translate lyrics text to target language
     */
    suspend fun translateLyrics(
        lyrics: String,
        targetLanguage: String = "English"
    ): kotlin.Result<String> {
        if (apiKey.isEmpty()) return Result.failure(Exception("Gemini API key is missing"))
        
        return withContext(Dispatchers.IO) {
            try {
                val response = client.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        GeminiRequest(
                            contents = listOf(
                                Content(
                                    parts = listOf(
                                        Part(text = "Translate the following lyrics to $targetLanguage while preserving the meaning, rhythm, and emotional tone. Maintain line breaks and structure:\n\n$lyrics")
                                    )
                                )
                            )
                        )
                    )
                }
                
                val geminiResponse: GeminiResponse = response.body()
                val translatedText = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: return@withContext Result.failure(Exception("No translation received from Gemini"))
                
                Result.success(translatedText)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

@Serializable
private data class GeminiRequest(
    val contents: List<Content>
)

@Serializable
private data class Content(
    val parts: List<Part>
)

@Serializable
private data class Part(
    val text: String
)

@Serializable
private data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
private data class Candidate(
    val content: Content? = null
)

