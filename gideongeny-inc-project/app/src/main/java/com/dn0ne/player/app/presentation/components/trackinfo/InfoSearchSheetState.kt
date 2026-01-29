package com.dn0ne.player.app.presentation.components.trackinfo

import com.dn0ne.player.app.domain.metadata.MetadataSearchResult

data class InfoSearchSheetState(
    val isLoading: Boolean = false,
    val searchResults: List<MetadataSearchResult> = emptyList()
)