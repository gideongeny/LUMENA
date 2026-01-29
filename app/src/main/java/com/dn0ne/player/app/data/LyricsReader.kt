package com.dn0ne.player.app.data

import com.dn0ne.player.app.domain.lyrics.Lyrics
import com.dn0ne.player.app.domain.result.DataError
import com.dn0ne.player.app.domain.result.Result
import com.dn0ne.player.app.domain.track.Track

interface LyricsReader {
    fun readFromTag(track: Track): Result<Lyrics?, DataError.Local>
}