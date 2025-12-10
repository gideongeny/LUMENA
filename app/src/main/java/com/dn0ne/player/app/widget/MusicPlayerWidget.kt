package com.dn0ne.player.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.dn0ne.player.MainActivity
import com.dn0ne.player.PlaybackService
import com.dn0ne.player.R
import com.dn0ne.player.app.domain.track.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MusicPlayerWidget : AppWidgetProvider() {
    private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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

    private suspend fun updateWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context, sessionToken).buildAsync()
        
        try {
            val controller = controllerFuture.await()
            val player = controller.player
            
            val currentTrack = player.currentMediaItem?.mediaMetadata
            val isPlaying = player.isPlaying
            val artwork = currentTrack?.artworkUri
            
            appWidgetIds.forEach { appWidgetId ->
                val views = RemoteViews(context.packageName, R.layout.widget_music_player)
                
                // Set track info
                views.setTextViewText(R.id.widget_track_title, currentTrack?.title ?: context.getString(R.string.unknown_title))
                views.setTextViewText(R.id.widget_track_artist, currentTrack?.artist ?: context.getString(R.string.unknown_artist))
                
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
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    companion object {
        fun updateWidget(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MusicPlayerWidget::class.java)
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_container)
        }
    }
}

