package com.dn0ne.player.app.data.repository

import com.dn0ne.player.app.domain.track.Track

interface TrackRepository {
    fun getTracks(): List<Track>
    fun getFoldersWithAudio(): Set<String>
}