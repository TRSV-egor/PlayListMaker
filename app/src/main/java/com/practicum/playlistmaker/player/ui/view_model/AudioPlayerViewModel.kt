package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.ui.models.PlayerStatus
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.DateFormater

class AudioPlayerViewModel : ViewModel() {

    companion object {
        private const val TIMER_UPD = 500L
    }

    private val handler = Handler(Looper.getMainLooper())

    private var mediaPlayer = MediaPlayer()


    private val playerStatusLiveDataMutable = MutableLiveData<PlayerStatus>()
    fun observerPlayer(): LiveData<PlayerStatus> = playerStatusLiveDataMutable


    fun fillPlayer(track: Track) {
        changePlayerStatus(
            PlayerStatus.Default(track)
        )
        prepareMediaPlayer(track.previewUrl)
    }

    fun prepareMediaPlayer(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            changePlayerStatus(
                PlayerStatus.Prepared
            )
        }

        mediaPlayer.setOnCompletionListener {
            changePlayerStatus(
                PlayerStatus.Prepared
            )
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

            else -> null
        }
    }

    fun releaseAudioPlayer() {
        mediaPlayer.release()
    }

    fun pauseMediaPlayer() {
        mediaPlayer.pause()

        changePlayerStatus(
            PlayerStatus.Paused
        )
    }

    private fun startMediaPlayer() {
        mediaPlayer.start()

        changePlayerStatus(
            PlayerStatus.Playing
        )

        handler.post(durationTimer())
    }

    private fun durationTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerStatusLiveDataMutable.value is PlayerStatus.Playing) {
                    PlayerStatus.Playing.timer = DateFormater.mmSS(mediaPlayer.currentPosition)
                    playerStatusLiveDataMutable.postValue(PlayerStatus.Playing)
                    handler.postDelayed(this, TIMER_UPD)
                } else {
                    handler.removeCallbacks(this)
                }
            }

        }

    }

    private fun changePlayerStatus(status: PlayerStatus) {
        playerStatusLiveDataMutable.postValue(status)
    }
}