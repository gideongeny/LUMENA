package com.dn0ne.player.app.presentation.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dn0ne.player.app.domain.track.Track
import kotlinx.coroutines.launch

val alphabet = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ#").map { it.toString() }

@Composable
fun AlphabetFastScroller(
    tracks: List<Track>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Map of letter to first index in the list
    val letterToIndex = remember(tracks) {
        val map = mutableMapOf<String, Int>()
        var currentIndex = 0
        // This depends on how the list is grouped in TrackList.kt
        // If sticky headers are used, we need to account for header items too.
        // For simplicity, let's just find the first track index for each letter.
        // In TrackList.kt: headers + tracks.
        // It's better to calculate the exact item index including headers.
        
        val grouped = tracks.groupBy { track ->
            val firstChar = track.title?.firstOrNull()?.uppercaseChar() ?: '#'
            if (firstChar.isLetter()) firstChar.toString() else "#"
        }.toSortedMap { a, b ->
            if (a == "#") 1 else if (b == "#") -1 else a.compareTo(b)
        }
        
        var totalOffset = 0
        grouped.forEach { (letter, groupTracks) ->
            map[letter] = totalOffset
            totalOffset += 1 + groupTracks.size // 1 for header + groupTracks.size
        }
        map
    }

    Column(
        modifier = modifier
            .width(24.dp)
            .fillMaxHeight()
            .pointerInput(letterToIndex) {
                detectDragGestures { change, _ ->
                    val y = change.position.y
                    val height = size.height
                    val index = ((y / height) * alphabet.size).toInt().coerceIn(0, alphabet.lastIndex)
                    val letter = alphabet[index]
                    letterToIndex[letter]?.let { listIndex ->
                        coroutineScope.launch {
                            listState.scrollToItem(listIndex)
                        }
                    }
                }
            }
            .pointerInput(letterToIndex) {
                detectTapGestures { offset ->
                    val y = offset.y
                    val height = size.height
                    val index = ((y / height) * alphabet.size).toInt().coerceIn(0, alphabet.lastIndex)
                    val letter = alphabet[index]
                    letterToIndex[letter]?.let { listIndex ->
                        coroutineScope.launch {
                            listState.scrollToItem(listIndex)
                        }
                    }
                }
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        alphabet.forEach { letter ->
            Text(
                text = letter,
                fontSize = 10.sp,
                color = if (letterToIndex.containsKey(letter)) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 1.dp)
            )
        }
    }
}
