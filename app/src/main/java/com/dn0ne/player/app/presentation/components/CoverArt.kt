package com.dn0ne.player.app.presentation.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest

@Composable
fun CoverArt(
    uri: Uri,
    onCoverArtLoaded: ((androidx.compose.ui.graphics.ImageBitmap?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .size(120, 120) // Limit size for performance
                .memoryCacheKey(uri.toString())
                .diskCacheKey(uri.toString())
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f),
            onSuccess = {
                Log.d("CoverArt", "Image loaded successfully for URI: $uri")
                // Callback provided but we don't do expensive bitmap conversion anymore
                onCoverArtLoaded?.invoke(null)
            },
            onError = {
                Log.e("CoverArt", "Failed to load image for URI: $uri")
                onCoverArtLoaded?.invoke(null)
            }
        )
        
        // Show placeholder icon while loading
        Icon(
            imageVector = Icons.Rounded.MusicNote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxSize(.4f)
        )
    }
}