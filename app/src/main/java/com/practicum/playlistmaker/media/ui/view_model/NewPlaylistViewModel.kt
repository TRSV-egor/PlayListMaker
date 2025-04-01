package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val imageLiveData = MutableLiveData(Pair("", false))
    fun observeState(): LiveData<Pair<String, Boolean>> = imageLiveData

    fun save(name: String, path: String, description: String) {
        viewModelScope.launch {
            playlistInteractor.add(name, path, description)
        }
    }

    fun imageLoaded(path: String) {
        imageLiveData.value = Pair(path, true)
    }
}