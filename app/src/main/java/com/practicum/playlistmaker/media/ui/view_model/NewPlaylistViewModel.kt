package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
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


    private val editorLiveDataMutable = MutableLiveData<PlaylistModel>()
    fun observerEditor(): LiveData<PlaylistModel> = editorLiveDataMutable

    fun fillPlaylistEditor(playlistModel: PlaylistModel?) {
        if (playlistModel == null) {
            return
        } else {
            editorLiveDataMutable.value = (playlistModel as PlaylistModel)
        }

    }

    fun update(name: String, path: String, description: String) {

        editorLiveDataMutable.value?.let {
            viewModelScope.launch {
                playlistInteractor.update(
                    PlaylistModel(
                        id = it.id,
                        name = name,
                        description = description,
                        path = path,
                        tracks = it.tracks
                    )
                )
            }

        }

    }

}