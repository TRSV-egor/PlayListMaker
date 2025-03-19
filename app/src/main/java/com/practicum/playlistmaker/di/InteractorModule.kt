package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractorImpl
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
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

    single<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(favoriteTrackRepository = get())
    }

}