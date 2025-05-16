package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.domain.db.impl.FavoriteTrackInteractorImpl
import com.practicum.playlistmaker.media.domain.db.impl.PlaylistInteractorImpl
import com.practicum.playlistmaker.media.domain.db.interfaces.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(
            repository = get(),
            executor = get()
        )
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(repository = get())
    }

    factory<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(favoriteTrackRepository = get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(playlistRepository = get())
    }

}