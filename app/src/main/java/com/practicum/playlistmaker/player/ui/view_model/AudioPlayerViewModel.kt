package com.practicum.playlistmaker.player.ui.view_model


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.ui.models.PlayerStatus
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.DateFormater

class AudioPlayerViewModel(
    private var mediaPlayer: AudioPlayerInteractor
) : ViewModel() {



    companion object {
        private const val TIMER_UPD = 500L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(
                    Creator.provideAudioPlayerInteractor()
                )
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    private val playerStatusLiveDataMutable = MutableLiveData<PlayerStatus>()
    fun observerPlayer(): LiveData<PlayerStatus> = playerStatusLiveDataMutable


    fun fillPlayer(track: Track) {
        changePlayerStatus(
            PlayerStatus.Default(track)
        )
        prepareMediaPlayer(track.previewUrl)
    }

    private fun prepareMediaPlayer(previewUrl: String) {
        mediaPlayer.prepare(previewUrl) { isTrackCompleted ->
            changePlayerStatus(
                PlayerStatus.Prepared(isTrackCompleted)
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

            else -> {}
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

        changePlayerStatus(
            PlayerStatus.Playing(PlayerStatus.ZERO_TIMER)
        )

        mediaPlayer.start()


        handler.post(durationTimer())

    }

    private fun durationTimer(): Runnable {
        return object : Runnable {
            override fun run() {

                if (playerStatusLiveDataMutable.value is PlayerStatus.Playing) {
//                    bug: (плавающая ошибка) при повторном начале воспроизведения таймер меняется 00:00, 00:29, 00:01
//                    связано с тем что mediaPlayer.getCurrentPosition() не успевает обновить значение 00:29 на 00:00 к моменту
//                    начала воспроизведения. На некоторых устройствах проявляется, а на некоторых нет
                    playerStatusLiveDataMutable.postValue(PlayerStatus.Playing(DateFormater.mmSS(mediaPlayer.getCurrentPosition())))
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