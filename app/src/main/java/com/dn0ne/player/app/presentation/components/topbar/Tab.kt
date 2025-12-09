package com.dn0ne.player.app.presentation.components.topbar

import androidx.annotation.StringRes
import com.dn0ne.player.R

enum class Tab(@StringRes val titleResId: Int) {
    Tracks(R.string.tracks),
    RecentlyPlayed(R.string.recently_played),
    Favorites(R.string.favorites),
    Playlists(R.string.playlists),
    Albums(R.string.albums),
    Artists(R.string.artists),
    Genres(R.string.genres),
    Folders(R.string.folders)
}