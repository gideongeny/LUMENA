package com.dn0ne.player.app.domain.track

val Track.format: String
    get() = data.substringAfterLast(".")