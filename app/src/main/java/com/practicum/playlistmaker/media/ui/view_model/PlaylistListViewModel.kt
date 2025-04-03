package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import kotlinx.coroutines.launch

class PlaylistListViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playLiveData = MutableLiveData<MutableList<PlaylistModel>>()
    fun observeState(): LiveData<MutableList<PlaylistModel>> = playLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylists()
                .collect { playlists ->
                    playLiveData.value = playlists.toMutableList()
                }
        }
    }

}