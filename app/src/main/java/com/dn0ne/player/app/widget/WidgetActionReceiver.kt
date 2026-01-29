package com.dn0ne.player.app.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.dn0ne.player.PlaybackService
import com.dn0ne.player.app.widget.WidgetActions
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WidgetActionReceiver : BroadcastReceiver() {
    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onReceive(context: Context, intent: Intent) {
        receiverScope.launch {
            try {
                // Start the service if it's not running
                val serviceIntent = Intent(context, PlaybackService::class.java)
                context.startForegroundService(serviceIntent)

                // Small delay to allow service to start
                kotlinx.coroutines.delay(500)

                val sessionToken = SessionToken(
                    context,
                    android.content.ComponentName(context, PlaybackService::class.java)
                )
                val controllerFuture: ListenableFuture<MediaController> =
                    MediaController.Builder(context, sessionToken).buildAsync()

                val controller = suspendCancellableCoroutine<MediaController> { continuation ->
                    controllerFuture.addListener({
                        try {
                            continuation.resume(controllerFuture.get())
                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }, MoreExecutors.directExecutor())
                }

                when (intent.action) {
                    WidgetActions.ACTION_PLAY_PAUSE -> {
                        if (controller.isPlaying) {
                            controller.pause()
                        } else {
                            controller.play()
                        }
                    }
                    WidgetActions.ACTION_NEXT -> {
                        if (controller.hasNextMediaItem()) {
                            controller.seekToNextMediaItem()
                        }
                    }
                    WidgetActions.ACTION_PREVIOUS -> {
                        if (controller.hasPreviousMediaItem()) {
                            controller.seekToPreviousMediaItem()
                        }
                    }
                    WidgetActions.ACTION_SHUFFLE -> {
                        controller.shuffleModeEnabled = !controller.shuffleModeEnabled
                    }
                    WidgetActions.ACTION_REPEAT -> {
                        val currentRepeatMode = controller.repeatMode
                        val nextRepeatMode = when (currentRepeatMode) {
                            androidx.media3.common.Player.REPEAT_MODE_OFF -> androidx.media3.common.Player.REPEAT_MODE_ALL
                            androidx.media3.common.Player.REPEAT_MODE_ALL -> androidx.media3.common.Player.REPEAT_MODE_ONE
                            androidx.media3.common.Player.REPEAT_MODE_ONE -> androidx.media3.common.Player.REPEAT_MODE_OFF
                            else -> androidx.media3.common.Player.REPEAT_MODE_OFF
                        }
                        controller.repeatMode = nextRepeatMode
                    }
                }

                // Update widget after action
                MusicPlayerWidget.updateWidget(context)

                controller.release()
            } catch (e: Exception) {
                android.util.Log.e("WidgetActionReceiver", "Failed to handle widget action", e)
            }
        }
    }
}
