package com.dn0ne.player.app.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.media3.common.MediaMetadata
import com.dn0ne.player.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt

class MusicPlayerWidgetProgress : MusicPlayerWidget() {
    override val layoutId: Int = R.layout.widget_music_player_progress

    override suspend fun updateWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Call super to handle the basic updates
        super.updateWidgets(context, appWidgetManager, appWidgetIds)

        // Now update progress for each widget
        val sessionToken = androidx.media3.session.SessionToken(context.applicationContext, ComponentName(context, com.dn0ne.player.PlaybackService::class.java))
        val controllerFuture = androidx.media3.session.MediaController.Builder(context.applicationContext, sessionToken).buildAsync()

        var controller: androidx.media3.session.MediaController? = null
        try {
            controller = suspendCancellableCoroutine<androidx.media3.session.MediaController> { continuation ->
                controllerFuture.addListener({
                    try {
                        continuation.resume(controllerFuture.get())
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }, com.google.common.util.concurrent.MoreExecutors.directExecutor())
            }

            val currentPosition = controller!!.currentPosition
            val duration = controller!!.duration

            val progress = if (duration > 0) {
                ((currentPosition.toFloat() / duration.toFloat()) * 100).roundToInt()
            } else {
                0
            }

            appWidgetIds.forEach { appWidgetId ->
                val views = RemoteViews(context.packageName, layoutId)
                views.setProgressBar(R.id.widget_progress, 100, progress, false)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        } catch (e: Exception) {
            // In case of error, set progress to 0
            appWidgetIds.forEach { appWidgetId ->
                val views = RemoteViews(context.packageName, layoutId)
                views.setProgressBar(R.id.widget_progress, 100, 0, false)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        } finally {
            controller?.release()
        }
    }

    companion object {
        fun updateWidget(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MusicPlayerWidgetProgress::class.java)
            )

            val updateIntent = Intent(context, MusicPlayerWidgetProgress::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(updateIntent)
        }
    }
}