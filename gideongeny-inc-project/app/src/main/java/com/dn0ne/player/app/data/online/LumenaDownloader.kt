package com.dn0ne.player.app.data.online

import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.io.ByteArrayOutputStream
import java.io.InputStream

class LumenaDownloader : Downloader() {
    companion object {
        const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
    }

    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val urlString = request.url()
        val url = URL(urlString)
        
        android.util.Log.d("LumenaDownloader", "Executing $httpMethod request to: $urlString")
        
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = httpMethod
        con.readTimeout = 30000
        con.connectTimeout = 30000
        con.setRequestProperty("User-Agent", USER_AGENT)
        
        request.headers().forEach { (key, list) ->
            list.forEach { value ->
                con.setRequestProperty(key, value)
            }
        }

        if (request.dataToSend() != null) {
            con.doOutput = true
            con.outputStream.write(request.dataToSend())
        }

        val code = con.responseCode
        val message = con.responseMessage
        
        android.util.Log.d("LumenaDownloader", "Response Code: $code, Message: $message")
        
        // Read response body
        val inputStream: InputStream? = try {
            con.inputStream
        } catch (e: IOException) {
            con.errorStream
        }

        val body = inputStream?.readBytes() ?: ByteArray(0)
        val bodyString = String(body, java.nio.charset.StandardCharsets.UTF_8)
        
        if (code != 200) {
            android.util.Log.w("LumenaDownloader", "Non-200 response. Body snippet: ${bodyString.take(500)}")
        } else if (bodyString.contains("playabilityStatus")) {
             // Log a bit of the playability status for debugging YouTube specifically
             val index = bodyString.indexOf("playabilityStatus")
             android.util.Log.d("LumenaDownloader", "Playability snippet: ${bodyString.substring(index, (index + 200).coerceAtMost(bodyString.length))}")
        }
        
        val headers = mutableMapOf<String, List<String>>()
        con.headerFields.forEach { (key, value) ->
            if (key != null && value != null) {
                headers[key] = value
            }
        }

        return Response(code, message, headers, bodyString, null)
    }
}
