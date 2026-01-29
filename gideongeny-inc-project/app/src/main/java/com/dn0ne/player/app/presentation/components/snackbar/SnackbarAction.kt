package com.dn0ne.player.app.presentation.components.snackbar

import androidx.annotation.StringRes

data class SnackbarAction(
    @StringRes val name: Int,
    val action: () -> Unit
)