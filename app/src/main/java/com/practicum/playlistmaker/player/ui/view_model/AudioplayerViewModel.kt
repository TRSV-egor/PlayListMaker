package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.util.DateFormater
import com.practicum.playlistmaker.player.domain.model.PlayerStateType
import com.practicum.playlistmaker.search.domain.models.Track

class AudioplayerViewModel : ViewModel() {

    companion object {
        private const val TIMER_UPD = 500L
    }

    private val handler = Handler(Looper.getMainLooper())

    private var mediaPlayer = MediaPlayer()

    private var playerStateTypeMutable = MutableLiveData<PlayerStateType>()
    var playerState: LiveData<PlayerStateType> = playerStateTypeMutable

    private var playerTimerMutable = MutableLiveData<String>()
    var playerTimer: LiveData<String> = playerTimerMutable


    private var currentTrackMutable = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = currentTrackMutable

    fun fillPlayer(track: Track) {
        currentTrackMutable.value = track
        playerStateTypeMutable.value = PlayerStateType.DEFAULT
    }

    fun prepareMediaPlayer(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerStateTypeMutable.value = PlayerStateType.PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playerStateTypeMutable.value = PlayerStateType.PREPARED
        }
    }

    fun playbackControl() {
        when (playerStateTypeMutable.value) {
            PlayerStateType.PLAYING -> {
                pauseMediaPlayer()
            }

            PlayerStateType.PREPARED, PlayerStateType.PAUSED -> {
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
        playerStateTypeMutable.value = PlayerStateType.PAUSED
    }

    private fun startMediaPlayer() {
        mediaPlayer.start()
        playerStateTypeMutable.value = PlayerStateType.PLAYING
        handler.post(durationTimer())
    }

    private fun durationTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerStateTypeMutable.value == PlayerStateType.PLAYING) {
                    playerTimerMutable.value = DateFormater.mmSS(mediaPlayer.currentPosition)
                    handler.postDelayed(this, TIMER_UPD)
                } else {
                    handler.removeCallbacks(this)
                }


            }

        }

    }
}