package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.data.converters.PlaylistDBConvertor
import com.practicum.playlistmaker.media.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.media.data.db.repo.FavoriteTrackRepositoryImpl
import com.practicum.playlistmaker.media.data.db.repo.PlaylistRepositoryImpl
import com.practicum.playlistmaker.media.domain.db.interfaces.FavoriteTrackRepository
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistRepository
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    factory<TracksRepository> {
        TracksRepositoryImpl(
            localData = get(),
            networkClient = get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(settingsLocalData = get())
    }

    factory { TrackDbConvertor() }

    factory { PlaylistDBConvertor() }

    factory<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(appDatabase = get(), trackDbConvertor = get())
    }

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(appDatabase = get(), playlistDbConvertor = get())
    }

}