package com.dn0ne.player.app.presentation.components.trackinfo

import com.dn0ne.player.app.domain.metadata.Metadata

data class ChangesSheetState(
    val isLoadingArt: Boolean = false,
    val isArtFromGallery: Boolean = false,
    val metadata: Metadata = Metadata()
)
