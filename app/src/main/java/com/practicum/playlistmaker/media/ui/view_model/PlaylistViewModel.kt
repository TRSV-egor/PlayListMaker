package com.practicum.playlistmaker.media.ui.view_model

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Declination
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistLiveDataMutable = MutableLiveData<PlaylistModel>()
    fun observerPlaylist(): LiveData<PlaylistModel> = playlistLiveDataMutable

    fun fillPlaylistViewer(playlistModel: PlaylistModel?) {
        if (playlistModel != null) {
            playlistLiveDataMutable.value = playlistModel as PlaylistModel
        }
    }

    fun removeTrackFromPlaylist(track: Track) {

        val playlistModel = playlistLiveDataMutable.value as PlaylistModel

        viewModelScope.launch {
            if (playlistInteractor.removeTrack(track, playlistModel)) {

                val result = playlistModel.tracks.toMutableList()
                result.remove(track)

                playlistLiveDataMutable.postValue(
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
            playlistInteractor.remove(playlistLiveDataMutable.value as PlaylistModel)
        }

    }

    fun updateFromDataBase() {
        if (playlistLiveDataMutable.isInitialized) {
            viewModelScope.launch {
                playlistInteractor.receivePlaylistById(playlistLiveDataMutable.value?.id as Long)
                    .collect { playlist ->
                        playlistLiveDataMutable.value = playlist
                    }
            }

        }
    }

    private fun prepareDataForShare(): String {

        var message = ""
        val trackList = playlistLiveDataMutable.value?.tracks ?: listOf()

        message += "${playlistLiveDataMutable.value?.name}\n"

        message += "${playlistLiveDataMutable.value?.description}\n"

        message += "${trackList.size} ${Declination.getTracks(trackList.size)} \n"

        for (i in trackList.indices) {
            message += "${i + 1}. ${trackList[i].artistName} - ${trackList[i].trackName} (${trackList[i].trackTime})\n"
        }
        return message

    }

    fun generateIntent(): Intent {

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, prepareDataForShare())
        intent.setType("text/plain")
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

}

