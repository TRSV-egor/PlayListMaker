package com.practicum.playlistmaker.di

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

    single<TracksRepository>{
        TracksRepositoryImpl(
            localData = get(),
            networkClient = get())
    }

    single<SettingsRepository>{
        SettingsRepositoryImpl(settingsLocalData = get())
    }

}