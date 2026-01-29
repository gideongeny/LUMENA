package com.dn0ne.player.app.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.dn0ne.player.app.domain.track.Track
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    onTrackClick: (Track) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search songs...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                    IconButton(onClick = { viewModel.onSearch() }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                searchResults.isNotEmpty() -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        // Separate local and YouTube results
                        val localResults = searchResults.filter { !it.data.startsWith("http") }
                        val youtubeResults = searchResults.filter { it.data.startsWith("http") }
                        
                        // Local results section
                        if (localResults.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Local Songs (${localResults.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(items = localResults, key = { it.data }) { track ->
                               TrackItem(track = track, onClick = {
                                   viewModel.resolveStreamUrl(track) { streamUrl ->
                                       val playableTrack = track.copy(data = streamUrl)
                                       onTrackClick(playableTrack)
                                   }
                               })
                            }
                        }
                        
                        // YouTube results section
                        if (youtubeResults.isNotEmpty()) {
                            item {
                                Text(
                                    text = "YouTube (${youtubeResults.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            items(items = youtubeResults, key = { it.data }) { track ->
                               TrackItem(track = track, onClick = {
                                   viewModel.resolveStreamUrl(track) { streamUrl ->
                                       val playableTrack = track.copy(data = streamUrl)
                                       onTrackClick(playableTrack)
                                   }
                               })
                            }
                        }
                    }
                }
                else -> {
                    val errorMsg by viewModel.errorMessage.collectAsState()
                    Text(
                        text = errorMsg ?: "Search for local or online songs",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TrackItem(track: Track, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(track.title ?: "Unknown Title", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(track.artist ?: "Unknown Artist", maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingContent = {
            AsyncImage(
                model = track.coverArtUri,
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
