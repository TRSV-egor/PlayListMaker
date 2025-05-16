package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.interfaces.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.player.ui.models.PlaylistStatus
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val trackLiveDataMutable = MutableLiveData<Track>()
    fun observerTrack(): LiveData<Track> = trackLiveDataMutable

    private val trackIsFavoriteLiveDataMutable = MutableLiveData<Boolean>()
    fun observerFavoriteTrack(): LiveData<Boolean> = trackIsFavoriteLiveDataMutable

    private val playlistLiveData = MutableLiveData<MutableList<PlaylistModel>>()
    fun observePlaylist(): LiveData<MutableList<PlaylistModel>> = playlistLiveData

    private val messageStatus = MutableLiveData<PlaylistStatus>()
    fun observeMessageStatus(): LiveData<PlaylistStatus> = messageStatus

    fun setTrack(track: Track) {
        trackLiveDataMutable.value = track
    }

    fun changeFavoriteStatus(track: Track) {
        viewModelScope.launch {
            trackIsFavoriteLiveDataMutable.value =
                favoriteTrackInteractor.addOrRemoveFavoriteTrack(track)
        }
    }

    fun checkFavoriteStatus(track: Track) {
        viewModelScope.launch {
            trackIsFavoriteLiveDataMutable.value = favoriteTrackInteractor.checkFavoriteTrack(track)
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylists()
                .collect { playlists ->
                    playlistLiveData.value = playlists.toMutableList()
                }
        }
    }

    fun addTrackToPlaylist(track: Track, playlistModel: PlaylistModel) {

        viewModelScope.launch {
            if (playlistInteractor.addTrack(track, playlistModel)) {
                messageStatus.value =
                    PlaylistStatus.Added(true, "Добавлено в плейлист ${playlistModel.name}")
            } else {
                messageStatus.value =
                    PlaylistStatus.Exist(true, "Трек уже добавлен в плейлист ${playlistModel.name}")
            }
        }
    }

    fun messageBeenSend() {
        messageStatus.value = PlaylistStatus.Default(false, "")
    }
}