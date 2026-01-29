package com.dn0ne.player.app.di

import com.dn0ne.player.BuildConfig
import com.dn0ne.player.app.data.online.SpotifyAuthManager
import com.dn0ne.player.app.data.online.SpotifyRepository
import com.dn0ne.player.app.data.remote.lyrics.YouTubeTranscriptsProvider
import com.dn0ne.player.app.data.online.YouTubeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val onlineFeaturesModule = module {
    single { 
        SpotifyAuthManager(
            context = androidContext(),
            clientId = BuildConfig.SPOTIFY_CLIENT_ID,
            clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET
        ) 
    }
    
    single { 
        SpotifyRepository(
            context = androidContext(),
            client = get(),
            authManager = get()
        ) 
    }
    
    single { 
        YouTubeTranscriptsProvider(
            context = androidContext(),
            client = get()
        ) 
    }

    single {
        YouTubeRepository(context = androidContext())
    }

    single {
        com.dn0ne.player.app.data.online.SponsorBlockRepository(client = get())
    }

    single {
        com.dn0ne.player.app.data.online.ReturnYouTubeDislikeRepository(client = get())
    }

    single {
        com.dn0ne.player.app.data.online.TranslationRepository(
            apiKey = BuildConfig.YOUTUBE_API_KEY,
            client = get()
        )
    }
}
