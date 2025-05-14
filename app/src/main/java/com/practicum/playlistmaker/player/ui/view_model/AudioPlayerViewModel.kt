package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.interfaces.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.ui.models.PlaylistStatus
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private var mediaPlayer: AudioPlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    //TODO вынести в сервсис
//    companion object {
//        private const val TIMER_UPD = 300L
//    }

    //TODO вынести в сервсис
//    private var timerJob: Job? = null

    //TODO вынести в сервсис
//    private val playerStatusLiveDataMutable = MutableLiveData<PlayerStatus>()
//    fun observerPlayer(): LiveData<PlayerStatus> = playerStatusLiveDataMutable

    private val trackIsFavoriteLiveDataMutable = MutableLiveData<Boolean>()
    fun observerFavoriteTrack(): LiveData<Boolean> = trackIsFavoriteLiveDataMutable

    private val playlistLiveData = MutableLiveData<MutableList<PlaylistModel>>()
    fun observePlaylist(): LiveData<MutableList<PlaylistModel>> = playlistLiveData

    private val messageStatus = MutableLiveData<PlaylistStatus>()
    fun observeMessageStatus(): LiveData<PlaylistStatus> = messageStatus

    //TODO вынести в сервсис
//    fun fillPlayer(track: Track) {
//        changePlayerStatus(
//            PlayerStatus.Default(track)
//        )
//        prepareMediaPlayer(track.previewUrl)
//        checkFavoriteStatus(track)
//    }

    //TODO вынести в сервсис
//    private fun prepareMediaPlayer(previewUrl: String) {
//        mediaPlayer.prepare(previewUrl) { isTrackCompleted ->
//            changePlayerStatus(
//                PlayerStatus.Prepared(isTrackCompleted)
//            )
//            timerJob?.cancel()
//        }
//
//    }

    //TODO вынести в сервсис
//    fun pauseMediaPlayer() {
//
//        mediaPlayer.pause()
//
//        timerJob?.cancel()
//
//        changePlayerStatus(
//            PlayerStatus.Paused
//        )
//
//    }

    //TODO перенести в фрагмент
//    fun playbackControl() {
//        when (playerStatusLiveDataMutable.value) {
//            is PlayerStatus.Playing -> {
//                pauseMediaPlayer()
//            }
//
//            is PlayerStatus.Prepared, PlayerStatus.Paused -> {
//                startMediaPlayer()
//            }
//
//            else -> {}
//        }
//    }

    //TODO вынести в сервсис
//    fun releaseAudioPlayer() {
//        mediaPlayer.release()
//    }


    //TODO вынести в сервсис
//    private fun startMediaPlayer() {
//
//        changePlayerStatus(
//            PlayerStatus.Playing(
//                DateFormater.mmSS(
//                    mediaPlayer.getCurrentPosition()
//                )
//            )
//        )
//
//        mediaPlayer.start()
//
//        durationTimer()
//
//    }

    //TODO вынести в сервсис
//    private fun durationTimer() {
//        timerJob = viewModelScope.launch {
//            while (playerStatusLiveDataMutable.value is PlayerStatus.Playing) {
//                delay(TIMER_UPD)
//                playerStatusLiveDataMutable.postValue(
//                    PlayerStatus.Playing(
//                        DateFormater.mmSS(
//                            mediaPlayer.getCurrentPosition()
//                        )
//                    )
//                )
//            }
//        }
//    }

    //TODO больше не надо
//    private fun changePlayerStatus(status: PlayerStatus) {
//        playerStatusLiveDataMutable.value = status
//    }

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