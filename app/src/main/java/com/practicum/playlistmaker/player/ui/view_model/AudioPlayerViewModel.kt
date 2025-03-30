package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel

import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.ui.models.PlayerStatus
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.DateFormater
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private var mediaPlayer: AudioPlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    companion object {
        private const val TIMER_UPD = 300L
    }

    private var timerJob: Job? = null

    private val playerStatusLiveDataMutable = MutableLiveData<PlayerStatus>()
    fun observerPlayer(): LiveData<PlayerStatus> = playerStatusLiveDataMutable

    private val trackIsFavoriteLiveDataMutable = MutableLiveData<Boolean>()
    fun observerFavoriteTrack(): LiveData<Boolean> = trackIsFavoriteLiveDataMutable

    private val playLiveData = MutableLiveData<MutableList<PlaylistModel>>()
    fun observePlaylist(): LiveData<MutableList<PlaylistModel>> = playLiveData

    fun fillPlayer(track: Track) {
        changePlayerStatus(
            PlayerStatus.Default(track)
        )
        prepareMediaPlayer(track.previewUrl)
        checkFavoriteStatus(track)
    }

    private fun prepareMediaPlayer(previewUrl: String) {
        mediaPlayer.prepare(previewUrl) { isTrackCompleted ->
            changePlayerStatus(
                PlayerStatus.Prepared(isTrackCompleted)
            )
            timerJob?.cancel()
        }

    }

    fun playbackControl() {
        when (playerStatusLiveDataMutable.value) {
            is PlayerStatus.Playing -> {
                pauseMediaPlayer()
            }

            is PlayerStatus.Prepared, PlayerStatus.Paused -> {
                startMediaPlayer()
            }

            else -> {}
        }
    }

    fun releaseAudioPlayer() {
        mediaPlayer.release()
    }

    fun pauseMediaPlayer() {
        mediaPlayer.pause()

        timerJob?.cancel()

        changePlayerStatus(
            PlayerStatus.Paused
        )
    }

    private fun startMediaPlayer() {

        changePlayerStatus(
            PlayerStatus.Playing(
                DateFormater.mmSS(
                    mediaPlayer.getCurrentPosition()
                )
            )
        )

        mediaPlayer.start()

        durationTimer()

    }

    private fun durationTimer() {
        timerJob = viewModelScope.launch {
            while (playerStatusLiveDataMutable.value is PlayerStatus.Playing) {
                delay(TIMER_UPD)
                playerStatusLiveDataMutable.postValue(
                    PlayerStatus.Playing(
                        DateFormater.mmSS(
                            mediaPlayer.getCurrentPosition()
                        )
                    )
                )
            }
        }
    }

    private fun changePlayerStatus(status: PlayerStatus) {
        playerStatusLiveDataMutable.value = status
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
                    playLiveData.value = playlists.toMutableList()
                }
        }
    }

    fun addTrackToPlaylist(track: Track, playlistModel: PlaylistModel) {
        viewModelScope.launch {
            playlistInteractor.update(track, playlistModel)
        }

    }
}