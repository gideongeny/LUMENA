package com.dn0ne.player.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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

class MusicPlayerWidget : AppWidgetProvider(), KoinComponent {
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

    private suspend fun updateWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context, sessionToken).buildAsync()
        
        try {
            val controller = suspendCancellableCoroutine<MediaController> { continuation ->
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
                val views = RemoteViews(context.packageName, R.layout.widget_music_player)
                
                // Set track info
                views.setTextViewText(R.id.widget_track_title, currentTrack?.title ?: context.getString(R.string.unknown_title))
                views.setTextViewText(R.id.widget_track_artist, currentTrack?.artist ?: context.getString(R.string.unknown_artist))
                
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
            }
            
            controller.release()
        } catch (e: Exception) {
            // Widget update failed, use default state
            appWidgetIds.forEach { appWidgetId ->
                val views = RemoteViews(context.packageName, R.layout.widget_music_player)
                views.setTextViewText(R.id.widget_track_title, context.getString(R.string.unknown_title))
                views.setTextViewText(R.id.widget_track_artist, context.getString(R.string.unknown_artist))
                views.setImageViewResource(R.id.widget_artwork, R.mipmap.ic_launcher)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
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
        
        // Build and execute the ImageRequest
        val request = ImageRequest.Builder(context)
            .data(finalUri)
            .build()
        
        val result = imageLoader.execute(request)
        
        // Use when statement to return bitmap on SuccessResult, null otherwise
        // Convert to software bitmap for RemoteViews compatibility
        return when (result) {
            is SuccessResult -> {
                val bitmap = result.image.toBitmap()
                // Ensure software bitmap for RemoteViews (hardware bitmaps are not supported)
                // Always copy to ARGB_8888 to ensure compatibility
                if (bitmap.config != android.graphics.Bitmap.Config.ARGB_8888) {
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
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MusicPlayerWidget::class.java)
            )
            
            // Create intent with ACTION_APPWIDGET_UPDATE and send as broadcast
            val updateIntent = Intent(context, MusicPlayerWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(updateIntent)
        }
    }
}
