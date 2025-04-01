package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val plaulistLiveDataMutable = MutableLiveData<PlaylistModel>()
    fun observerPlaylist(): LiveData<PlaylistModel> = plaulistLiveDataMutable

    fun fillPlaylistViewer(playlistModel: PlaylistModel?) {
        if (playlistModel != null) {
            plaulistLiveDataMutable.value = playlistModel as PlaylistModel
        }
    }

    fun removeTrackFromPlaylist(track: Track) {

        val playlistModel = plaulistLiveDataMutable.value as PlaylistModel

        viewModelScope.launch {
            if (playlistInteractor.removeTrack(track, playlistModel)) {

                val result = playlistModel.tracks.toMutableList()
                result.remove(track)

                plaulistLiveDataMutable.postValue(
                    with(playlistModel) {
                        return@with PlaylistModel(
                            id = id,
                            tracks = result.toList(),
                            name = name,
                            description = description,
                            path = path,
                        )
                    })
            }
        }

    }

    fun delete() {
        viewModelScope.launch {
            playlistInteractor.remove(plaulistLiveDataMutable.value as PlaylistModel)
        }

    }

}