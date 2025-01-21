package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<AudioPlayerInteractor>{
        AudioPlayerInteractorImpl(repository = get())
    }

    single<TracksInteractor>{
        TracksInteractorImpl(
            repository = get(),
            executor = get()
        )
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            externalNavigator = get()
        )
    }

}