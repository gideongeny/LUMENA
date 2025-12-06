package com.dn0ne.player.app.presentation.components.trackinfo

import com.dn0ne.player.app.domain.lyrics.Lyrics

data class LyricsControlSheetState(
    val lyricsFromTag: Lyrics? = null,
    val lyricsFromRepository: Lyrics? = null,
    val isWritingToTag: Boolean = false,
    val isReadingFromFile: Boolean = false,
    val isFetchingFromRemote: Boolean = false,
    val isPublishingOnRemote: Boolean = false
)
