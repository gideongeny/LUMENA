package com.dn0ne.player.app.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.dn0ne.player.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WidgetActionReceiver : BroadcastReceiver() {
    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onReceive(context: Context, intent: Intent) {
        receiverScope.launch {
            val sessionToken = SessionToken(context, android.content.ComponentName(context, PlaybackService::class.java))
            val controllerFuture: ListenableFuture<MediaController> = MediaController.Builder(context, sessionToken).buildAsync()
            
            try {
                val controller = controllerFuture.await()
                val player = controller.player
                
                when (intent.action) {
                    WidgetActions.ACTION_PLAY_PAUSE -> {
                        if (player.isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    }
                    WidgetActions.ACTION_NEXT -> {
                        if (player.hasNextMediaItem()) {
                            player.seekToNextMediaItem()
                        }
                    }
                    WidgetActions.ACTION_PREVIOUS -> {
                        if (player.hasPreviousMediaItem()) {
                            player.seekToPreviousMediaItem()
                        }
                    }
                }
                
                // Update widget after action
                MusicPlayerWidget.updateWidget(context)
                
                controller.release()
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
}

object WidgetActions {
    const val ACTION_PLAY_PAUSE = "com.dn0ne.player.widget.ACTION_PLAY_PAUSE"
    const val ACTION_NEXT = "com.dn0ne.player.widget.ACTION_NEXT"
    const val ACTION_PREVIOUS = "com.dn0ne.player.widget.ACTION_PREVIOUS"
}

