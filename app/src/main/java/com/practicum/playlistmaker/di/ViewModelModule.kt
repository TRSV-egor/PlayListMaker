package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.ui.MediaViewModel
import com.practicum.playlistmaker.media.ui.fragments.FavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.fragments.PlaylistViewModel
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<AudioPlayerViewModel> {
        AudioPlayerViewModel(mediaPlayer = get())
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
        FavoriteTracksViewModel()
    }

    viewModel<PlaylistViewModel> {
        PlaylistViewModel()
    }

    viewModel<MediaViewModel> {
        MediaViewModel()
    }
}