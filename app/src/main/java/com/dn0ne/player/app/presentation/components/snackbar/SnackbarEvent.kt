package com.dn0ne.player.app.presentation.components.snackbar

import androidx.annotation.StringRes

data class SnackbarEvent(
    @StringRes val message: Int? = null,
    val rawMessage: String? = null,
    val action: SnackbarAction? = null
) {
    // Companion or secondary constructor to maintain compatibility
    constructor(@StringRes message: Int, action: SnackbarAction? = null) : this(message, null, action)
}