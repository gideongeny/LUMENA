package com.dn0ne.player.auto

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.dn0ne.player.app.data.repository.TrackRepository
import com.dn0ne.player.app.domain.track.Track
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.guava.future
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android Auto MediaBrowser callback implementation
 * Provides browsable music library structure for Android Auto
 */
class AutoMediaBrowserCallback(
    private val context: Context
) : MediaLibraryService.MediaLibrarySession.Callback, KoinComponent {

    private val trackRepository: TrackRepository by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        // Root menu IDs
        const val ROOT_ID = "root"
        const val TRACKS_ROOT = "tracks"
        const val ALBUMS_ROOT = "albums"
        const val ARTISTS_ROOT = "artists"
        const val PLAYLISTS_ROOT = "playlists"
        const val FAVORITES_ROOT = "favorites"
        const val RECENT_ROOT = "recent"
        
        // Prefixes for item IDs
        const val TRACK_PREFIX = "track_"
        const val ALBUM_PREFIX = "album_"
        const val ARTIST_PREFIX = "artist_"
        const val PLAYLIST_PREFIX = "playlist_"
    }

    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val rootItem = MediaItem.Builder()
            .setMediaId(ROOT_ID)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                    .setTitle("Lumena")
                    .build()
            )
            .build()

        return Futures.immediateFuture(
            LibraryResult.ofItem(rootItem, params)
        )
    }

    override fun onGetChildren(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> = scope.future {
        val children = when (parentId) {
            ROOT_ID -> getRootChildren()
            TRACKS_ROOT -> getAllTracks()
            ALBUMS_ROOT -> getAllAlbums()
            ARTISTS_ROOT -> getAllArtists()
            PLAYLISTS_ROOT -> getAllPlaylists()
            FAVORITES_ROOT -> getFavoriteTracks()
            RECENT_ROOT -> getRecentTracks()
            else -> when {
                parentId.startsWith(ALBUM_PREFIX) -> getAlbumTracks(parentId.removePrefix(ALBUM_PREFIX))
                parentId.startsWith(ARTIST_PREFIX) -> getArtistTracks(parentId.removePrefix(ARTIST_PREFIX))
                parentId.startsWith(PLAYLIST_PREFIX) -> getPlaylistTracks(parentId.removePrefix(PLAYLIST_PREFIX))
                else -> emptyList()
            }
        }

        LibraryResult.ofItemList(children, params)
    }

    override fun onGetItem(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> = scope.future {
        val item = when {
            mediaId.startsWith(TRACK_PREFIX) -> {
                val trackId = mediaId.removePrefix(TRACK_PREFIX)
                trackRepository.getTrackById(trackId)?.toMediaItem()
            }
            else -> null
        }

        if (item != null) {
            LibraryResult.ofItem(item, null)
        } else {
            LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
        }
    }

    private fun getRootChildren(): List<MediaItem> {
        return listOf(
            createBrowsableMediaItem(
                RECENT_ROOT,
                "Recently Played",
                MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS
            ),
            createBrowsableMediaItem(
                FAVORITES_ROOT,
                "Favorites",
                MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS
            ),
            createBrowsableMediaItem(
                TRACKS_ROOT,
                "All Tracks",
                MediaMetadata.MEDIA_TYPE_FOLDER_MUSIC_ARTISTS
            ),
            createBrowsableMediaItem(
                ALBUMS_ROOT,
                "Albums",
                MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS
            ),
            createBrowsableMediaItem(
                ARTISTS_ROOT,
                "Artists",
                MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS
            ),
            createBrowsableMediaItem(
                PLAYLISTS_ROOT,
                "Playlists",
                MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS
            )
        )
    }

    private suspend fun getAllTracks(): List<MediaItem> {
        return trackRepository.getAllTracks()
            .take(100) // Limit for performance
            .map { it.toMediaItem() }
    }

    private suspend fun getAllAlbums(): List<MediaItem> {
        val albums = trackRepository.getAllTracks()
            .groupBy { it.album }
            .entries
            .sortedBy { it.key }
            .take(50) // Limit for performance

        return albums.map { (album, tracks) ->
            val firstTrack = tracks.firstOrNull()
            createBrowsableMediaItem(
                "$ALBUM_PREFIX$album",
                album ?: "Unknown Album",
                MediaMetadata.MEDIA_TYPE_ALBUM,
                firstTrack?.albumArtUri
            )
        }
    }

    private suspend fun getAllArtists(): List<MediaItem> {
        val artists = trackRepository.getAllTracks()
            .groupBy { it.artist }
            .entries
            .sortedBy { it.key }
            .take(50) // Limit for performance

        return artists.map { (artist, tracks) ->
            val firstTrack = tracks.firstOrNull()
            createBrowsableMediaItem(
                "$ARTIST_PREFIX$artist",
                artist ?: "Unknown Artist",
                MediaMetadata.MEDIA_TYPE_ARTIST,
                firstTrack?.albumArtUri
            )
        }
    }

    private suspend fun getAllPlaylists(): List<MediaItem> {
        return trackRepository.getAllPlaylists().map { playlist ->
            createBrowsableMediaItem(
                "$PLAYLIST_PREFIX${playlist.id}",
                playlist.name,
                MediaMetadata.MEDIA_TYPE_PLAYLIST
            )
        }
    }

    private suspend fun getFavoriteTracks(): List<MediaItem> {
        return trackRepository.getFavoriteTracks()
            .take(100)
            .map { it.toMediaItem() }
    }

    private suspend fun getRecentTracks(): List<MediaItem> {
        return trackRepository.getRecentlyPlayedTracks()
            .take(50)
            .map { it.toMediaItem() }
    }

    private suspend fun getAlbumTracks(album: String): List<MediaItem> {
        return trackRepository.getAllTracks()
            .filter { it.album == album }
            .sortedBy { it.trackNumber }
            .map { it.toMediaItem() }
    }

    private suspend fun getArtistTracks(artist: String): List<MediaItem> {
        return trackRepository.getAllTracks()
            .filter { it.artist == artist }
            .sortedBy { it.title }
            .map { it.toMediaItem() }
    }

    private suspend fun getPlaylistTracks(playlistId: String): List<MediaItem> {
        val playlist = trackRepository.getPlaylistById(playlistId)
        return playlist?.tracks?.map { it.toMediaItem() } ?: emptyList()
    }

    private fun createBrowsableMediaItem(
        mediaId: String,
        title: String,
        @MediaMetadata.MediaType mediaType: Int,
        artworkUri: Uri? = null
    ): MediaItem {
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .setMediaType(mediaType)
                    .setTitle(title)
                    .setArtworkUri(artworkUri)
                    .build()
            )
            .build()
    }

    private fun Track.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId("$TRACK_PREFIX$id")
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(albumArtUri)
                    .setTrackNumber(trackNumber)
                    .setGenre(genre)
                    .build()
            )
            .build()
    }
}
