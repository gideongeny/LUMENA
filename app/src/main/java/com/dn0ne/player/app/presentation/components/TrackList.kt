package com.dn0ne.player.app.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dn0ne.player.app.domain.track.Track

/**
 * Groups tracks by the first letter of their title (or "#" for non-alphabetic/numeric)
 */
private fun groupTracksAlphabetically(tracks: List<Track>): Map<String, List<Track>> {
    return tracks.groupBy { track ->
        val firstChar = track.title?.firstOrNull()?.uppercaseChar() ?: '#'
        when {
            firstChar.isLetter() -> firstChar.toString()
            firstChar.isDigit() -> "#"
            else -> "#"
        }
    }.toSortedMap { a, b ->
        when {
            a == "#" && b == "#" -> 0
            a == "#" -> 1
            b == "#" -> -1
            else -> a.compareTo(b)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.trackList(
    trackList: List<Track>,
    currentTrack: Track?,
    onTrackClick: (Track) -> Unit,
    onPlayNextClick: (Track) -> Unit,
    onAddToQueueClick: (Track) -> Unit,
    onAddToPlaylistClick: (Track) -> Unit,
    onViewTrackInfoClick: (Track) -> Unit,
    onGoToAlbumClick: (Track) -> Unit,
    onGoToArtistClick: (Track) -> Unit,
    onToggleFavoriteClick: ((Track) -> Unit)? = null,
    isFavorite: ((Track) -> Boolean)? = null,
    onLongClick: (Track) -> Unit = {},
    showAlphabeticalHeaders: Boolean = true
) {
    if (trackList.isEmpty()) {
        item(key = "empty") {
            NothingYet()
        }
        return
    }

    if (showAlphabeticalHeaders) {
        // Optimize: Pre-compute grouping once
        val groupedTracks = groupTracksAlphabetically(trackList)
        var globalTrackNumber = 1
        groupedTracks.forEach { (letter, tracks) ->
            stickyHeader(key = "header_$letter") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Optimize: Use key for better list performance
            items(
                items = tracks,
                key = { it.uri.toString() }
            ) { track ->
                TrackListItem(
                    track = track,
                    isCurrent = currentTrack?.uri == track.uri,
                    onClick = { onTrackClick(track) },
                    onLongClick = { onLongClick(track) },
                    onPlayNextClick = { onPlayNextClick(track) },
                    onAddToQueueClick = { onAddToQueueClick(track) },
                    onAddToPlaylistClick = { onAddToPlaylistClick(track) },
                    onViewTrackInfoClick = { onViewTrackInfoClick(track) },
                    onGoToAlbumClick = { onGoToAlbumClick(track) },
                    onGoToArtistClick = { onGoToArtistClick(track) },
                    onToggleFavoriteClick = onToggleFavoriteClick?.let { { it(track) } },
                    isFavorite = isFavorite?.invoke(track) ?: false,
                    trackNumber = globalTrackNumber++,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .animateItem(fadeInSpec = null, fadeOutSpec = null)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    } else {
        // Optimize: Use itemsIndexed for better performance with large lists
        itemsIndexed(
            items = trackList,
            key = { index, _ -> index }
        ) { index, track ->
            TrackListItem(
                track = track,
                isCurrent = currentTrack?.uri == track.uri,
                onClick = { onTrackClick(track) },
                onLongClick = { onLongClick(track) },
                onPlayNextClick = { onPlayNextClick(track) },
                onAddToQueueClick = { onAddToQueueClick(track) },
                onAddToPlaylistClick = { onAddToPlaylistClick(track) },
                onViewTrackInfoClick = { onViewTrackInfoClick(track) },
                onGoToAlbumClick = { onGoToAlbumClick(track) },
                onGoToArtistClick = { onGoToArtistClick(track) },
                onToggleFavoriteClick = onToggleFavoriteClick?.let { { it(track) } },
                isFavorite = isFavorite?.invoke(track) ?: false,
                trackNumber = index + 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

fun LazyGridScope.trackList(
    trackList: List<Track>,
    currentTrack: Track?,
    onTrackClick: (Track) -> Unit,
    onPlayNextClick: (Track) -> Unit,
    onAddToQueueClick: (Track) -> Unit,
    onAddToPlaylistClick: (Track) -> Unit,
    onViewTrackInfoClick: (Track) -> Unit,
    onGoToAlbumClick: (Track) -> Unit,
    onGoToArtistClick: (Track) -> Unit,
    onToggleFavoriteClick: ((Track) -> Unit)? = null,
    isFavorite: ((Track) -> Boolean)? = null,
    onLongClick: (Track) -> Unit,
) {
    if (trackList.isEmpty()) {
        item(key = "empty") {
            NothingYet()
        }
        return
    }

    items(
        items = trackList,
        key = { it.uri.toString() }
    ) { track ->
        TrackListItem(
            track = track,
            isCurrent = currentTrack?.uri == track.uri,
            onClick = { onTrackClick(track) },
            onLongClick = { onLongClick(track) },
            onPlayNextClick = { onPlayNextClick(track) },
            onAddToQueueClick = { onAddToQueueClick(track) },
            onAddToPlaylistClick = { onAddToPlaylistClick(track) },
            onViewTrackInfoClick = { onViewTrackInfoClick(track) },
            onGoToAlbumClick = { onGoToAlbumClick(track) },
            onGoToArtistClick = { onGoToArtistClick(track) },
            onToggleFavoriteClick = onToggleFavoriteClick?.let { { it(track) } },
            isFavorite = isFavorite?.invoke(track) ?: false,
            modifier = Modifier
                .fillMaxWidth()
                .animateItem(fadeInSpec = null, fadeOutSpec = null)
        )
    }
}