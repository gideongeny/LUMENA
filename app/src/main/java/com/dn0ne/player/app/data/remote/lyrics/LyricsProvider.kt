package com.dn0ne.player.app.data.remote.lyrics

import com.dn0ne.player.app.domain.lyrics.Lyrics
import com.dn0ne.player.app.domain.result.DataError
import com.dn0ne.player.app.domain.result.Result
import com.dn0ne.player.app.domain.track.Track

interface LyricsProvider {
    suspend fun getLyrics(track: Track): Result<Lyrics, DataError.Network>
    suspend fun postLyrics(track: Track, lyrics: Lyrics): Result<Unit, DataError.Network>
}