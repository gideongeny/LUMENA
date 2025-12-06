package com.dn0ne.player.setup.di

import com.dn0ne.player.app.data.repository.TrackRepository
import com.dn0ne.player.setup.data.SetupState
import com.dn0ne.player.setup.presentation.SetupViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val setupModule = module {
    single<SetupState> {
        SetupState(context = androidContext())
    }

    viewModel<SetupViewModel> {
        SetupViewModel(
            setupState = get(),
            settings = get(),
            musicScanner = get(),
            getFoldersWithAudio = get<TrackRepository>()::getFoldersWithAudio
        )
    }
}