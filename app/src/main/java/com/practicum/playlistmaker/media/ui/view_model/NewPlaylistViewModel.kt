package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun save(playlistModel: PlaylistModel) {
        viewModelScope.launch {
            playlistInteractor.add(playlistModel)
        }
    }
}