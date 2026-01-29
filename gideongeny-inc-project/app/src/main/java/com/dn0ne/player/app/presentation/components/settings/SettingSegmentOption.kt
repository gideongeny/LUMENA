package com.dn0ne.player.app.presentation.components.settings

import androidx.compose.ui.graphics.vector.ImageVector

data class SettingSegmentOption(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)