package com.dn0ne.player.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.dn0ne.player.MainActivity
import com.dn0ne.player.PlaybackService
import com.dn0ne.player.R
import com.dn0ne.player.app.data.remote.metadata.MetadataFetcher
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class MusicPlayerWidget : AppWidgetProvider(), KoinComponent {
    protected open val layoutId: Int = R.layout.widget_music_player
    private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val imageLoader: ImageLoader by inject()
    private val metadataFetcher: MetadataFetcher by inject()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        widgetScope.launch {
            updateWidgets(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onEnabled(context: Context) {
        widgetScope.launch {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MusicPlayerWidget::class.java)
            )
            updateWidgets(context, appWidgetManager, appWidgetIds)
        }
    }

    override fun onDisabled(context: Context) {
        widgetScope.cancel()
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (intent.action == PlaybackService.ACTION_PLAYER_STATE_CHANGED) {
            updateWidget(context)
        }
    }

    protected open suspend fun updateWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val sessionToken = SessionToken(context.applicationContext, ComponentName(context, PlaybackService::class.java))
        val controllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context.applicationContext, sessionToken).buildAsync()
        
        var controller: MediaController? = null
        try {
            controller = suspendCancellableCoroutine<MediaController> { continuation ->
                controllerFuture.addListener({
                    try {
                        continuation.resume(controllerFuture.get())
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }, MoreExecutors.directExecutor())
            }
            
            val currentTrack = controller.currentMediaItem?.mediaMetadata
            val isPlaying = controller.isPlaying
            
            // Get artwork bitmap - network calls wrapped in withContext(Dispatchers.IO)
            val artworkBitmap = withContext(Dispatchers.IO) {
                getArtworkBitmap(context, currentTrack)
            }
            
            appWidgetIds.forEach { appWidgetId ->
                try {
                    val views = RemoteViews(context.packageName, layoutId)
                    
                    // Set track info
                    views.setTextViewText(R.id.widget_track_title, currentTrack?.title ?: context.getString(R.string.unknown_title))
                    views.setTextViewText(R.id.widget_track_artist, currentTrack?.artist ?: context.getString(R.string.unknown_artist))
                    
                    // Set album for large widget if the view exists
                    val albumTitle = currentTrack?.albumTitle?.toString()
                    if (albumTitle != null && layoutId == R.layout.widget_music_player_large) {
                        views.setTextViewText(R.id.widget_track_album, albumTitle)
                    }
                    
                    // Set artwork
                    if (artworkBitmap != null) {
                        views.setImageViewBitmap(R.id.widget_artwork, artworkBitmap)
                    } else {
                        views.setImageViewResource(R.id.widget_artwork, R.mipmap.ic_launcher)
                    }
                    
                    // Set play/pause button - using Android built-in icons
                    val playPauseIcon = if (isPlaying) {
                        android.R.drawable.ic_media_pause
                    } else {
                        android.R.drawable.ic_media_play
                    }
                    views.setImageViewResource(R.id.widget_play_pause, playPauseIcon)
                    
                    // Set click intents
                    val mainIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val mainPendingIntent = PendingIntent.getActivity(
                        context, 0, mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_container, mainPendingIntent)
                    
                    val playPauseIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                        action = WidgetActions.ACTION_PLAY_PAUSE
                    }
                    val playPausePendingIntent = PendingIntent.getBroadcast(
                        context, 0, playPauseIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_play_pause, playPausePendingIntent)
                    
                    val nextIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                        action = WidgetActions.ACTION_NEXT
                    }
                    val nextPendingIntent = PendingIntent.getBroadcast(
                        context, 0, nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_next, nextPendingIntent)
                    
                    val prevIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                        action = WidgetActions.ACTION_PREVIOUS
                    }
                    val prevPendingIntent = PendingIntent.getBroadcast(
                        context, 0, prevIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_previous, prevPendingIntent)
                    
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    android.util.Log.e("MusicPlayerWidget", "Failed to update widget $appWidgetId", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MusicPlayerWidget", "Failed to get MediaController for widget update", e)
            // Fallback: Update to empty state if controller can't be reached
            appWidgetIds.forEach { appWidgetId ->
                val views = RemoteViews(context.packageName, layoutId)
                views.setTextViewText(R.id.widget_track_title, context.getString(R.string.unknown_title))
                views.setTextViewText(R.id.widget_track_artist, context.getString(R.string.unknown_artist))
                views.setImageViewResource(R.id.widget_artwork, R.mipmap.ic_launcher)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        } finally {
            controller?.release()
        }
    }

    private suspend fun getArtworkBitmap(context: Context, metadata: MediaMetadata?): Bitmap? {
        // First, determine the final Uri to use
        val artworkUri: Uri? = metadata?.artworkUri
        
        val finalUri: Uri? = if (artworkUri != null) {
            // Use local embedded art
            artworkUri
        } else {
            // Try to fetch from online - ensure both artist and album are non-null
            val artist = metadata?.artist?.toString()
            val album = metadata?.albumTitle?.toString()
            
            if (artist != null && album != null) {
                val onlineUrl = metadataFetcher.fetchAlbumArtUrl(artist, album)
                onlineUrl?.let { Uri.parse(it) }
            } else {
                null
            }
        }
        
        // If no Uri found, return null immediately
        if (finalUri == null) {
            return null
        }
        
        // Build and execute the ImageRequest with explicit size to prevent TransactionTooLargeException
        val request = ImageRequest.Builder(context)
            .data(finalUri)
            .size(300, 300) // Limit size for widgets
            .build()
        
        val result = imageLoader.execute(request)
        
        // Use when statement to return bitmap on SuccessResult, null otherwise
        // Convert to software bitmap for RemoteViews compatibility
        return when (result) {
            is SuccessResult -> {
                val bitmap = result.image.toBitmap()
                // Ensure software bitmap for RemoteViews (hardware bitmaps are not supported)
                // Always copy to ARGB_8888 to ensure compatibility
                val isHardware = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && 
                                bitmap.config == android.graphics.Bitmap.Config.HARDWARE
                if (bitmap.config != android.graphics.Bitmap.Config.ARGB_8888 || isHardware) {
                    bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, false)
                } else {
                    bitmap
                }
            }
            else -> null
        }
    }

    companion object {
        fun updateWidget(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            
            // Update Small Widget
            val smallIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MusicPlayerWidget::class.java)
            )
            val smallIntent = Intent(context, MusicPlayerWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, smallIds)
            }
            context.sendBroadcast(smallIntent)

            // Update Large Widget
            MusicPlayerWidgetLarge.updateWidget(context)
        }
    }
}
