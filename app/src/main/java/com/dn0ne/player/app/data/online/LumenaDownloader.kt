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
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    }

    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = URL(request.url())
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
        
        // Read response body
        val inputStream: InputStream = try {
            con.inputStream
        } catch (e: IOException) {
            con.errorStream
        }

        val body = inputStream?.readBytes() ?: ByteArray(0)
        
        val headers = mutableMapOf<String, List<String>>()
        con.headerFields.forEach { (key, value) ->
            if (key != null && value != null) {
                headers[key] = value
            }
        }

        return Response(code, message, headers, String(body, java.nio.charset.StandardCharsets.UTF_8), null) // latestUrl is null for now
    }
}
