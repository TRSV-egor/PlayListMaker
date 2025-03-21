package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackRepository
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackRepositoryImpl
import com.practicum.playlistmaker.player.data.impl.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.AudioPlayerRepository
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<AudioPlayerRepository>{
        AudioPlayerRepositoryImpl(mediaPlayerClient = get())
    }

    factory<TracksRepository> {
        TracksRepositoryImpl(
            localData = get(),
            networkClient = get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(settingsLocalData = get())
    }

    factory { TrackDbConvertor() }

    factory<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(appDatabase = get(), trackDbConvertor = get())
    }

}