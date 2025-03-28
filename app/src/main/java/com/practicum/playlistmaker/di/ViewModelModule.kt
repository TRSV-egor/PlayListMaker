package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.view_model.MediaViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<AudioPlayerViewModel> {
        AudioPlayerViewModel(mediaPlayer = get(), favoriteTrackInteractor = get())
    }

    viewModel<SearchViewModel> {
        SearchViewModel(tracksInteractor = get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(
            settingsInteractor = get()
        )
    }

    viewModel<FavoriteTracksViewModel> {
        FavoriteTracksViewModel(favoriteTrackInteractor = get())
    }

    viewModel<PlaylistViewModel> {
        PlaylistViewModel()
    }

    viewModel<MediaViewModel> {
        MediaViewModel()
    }

    viewModel<PlaylistViewModel> {
        PlaylistViewModel()
    }
}