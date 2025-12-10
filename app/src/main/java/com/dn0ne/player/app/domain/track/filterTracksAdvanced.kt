package com.dn0ne.player.app.domain.track

import androidx.compose.ui.util.fastFilter
import java.text.NumberFormat
import java.util.Locale

enum class FilterType {
    FIRST_LETTER,
    POSITION,
    SIZE,
    SEARCH
}

data class TrackFilter(
    val type: FilterType,
    val value: String = "",
    val minSize: Long? = null,
    val maxSize: Long? = null,
    val minPosition: Int? = null,
    val maxPosition: Int? = null
)

fun List<Track>.filterTracksAdvanced(filter: TrackFilter?): List<Track> {
    if (filter == null) return this
    
    return when (filter.type) {
        FilterType.FIRST_LETTER -> {
            if (filter.value.isBlank()) return this
            val letter = filter.value.firstOrNull()?.uppercaseChar() ?: return this
            fastFilter { track ->
                track.title?.firstOrNull()?.uppercaseChar() == letter ||
                track.artist?.firstOrNull()?.uppercaseChar() == letter ||
                track.album?.firstOrNull()?.uppercaseChar() == letter
            }
        }
        
        FilterType.POSITION -> {
            fastFilter { track ->
                val position = this.indexOf(track)
                (filter.minPosition == null || position >= filter.minPosition) &&
                (filter.maxPosition == null || position <= filter.maxPosition)
            }
        }
        
        FilterType.SIZE -> {
            fastFilter { track ->
                (filter.minSize == null || track.size >= filter.minSize) &&
                (filter.maxSize == null || track.size <= filter.maxSize)
            }
        }
        
        FilterType.SEARCH -> {
            filterTracks(filter.value)
        }
    }
}

fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format(Locale.getDefault(), "%.2f GB", gb)
        mb >= 1 -> String.format(Locale.getDefault(), "%.2f MB", mb)
        kb >= 1 -> String.format(Locale.getDefault(), "%.2f KB", kb)
        else -> "$bytes B"
    }
}

