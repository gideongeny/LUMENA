package com.dn0ne.player.setup.data

import android.content.Context

class SetupState(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("setup", Context.MODE_PRIVATE)
    private val isCompleteKey = "is-complete"

    var isComplete: Boolean
        get() = sharedPreferences.getBoolean(isCompleteKey, false)
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean(isCompleteKey, value)
                apply()
            }
        }
}