package com.dn0ne.player.app.presentation.components.settings

sealed class SettingOption(
    val title: String,
    val onSelection: () -> Unit,
)