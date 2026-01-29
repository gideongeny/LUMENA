package com.dn0ne.player.app.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.dn0ne.player.R

class MusicPlayerWidgetMini : MusicPlayerWidget() {
    override val layoutId: Int = R.layout.widget_music_player_mini

    companion object {
        fun updateWidget(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MusicPlayerWidgetMini::class.java)
            )

            val updateIntent = Intent(context, MusicPlayerWidgetMini::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(updateIntent)
        }
    }
}