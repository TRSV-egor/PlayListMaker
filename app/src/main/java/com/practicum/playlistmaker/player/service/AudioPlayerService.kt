package com.practicum.playlistmaker.player.service

import android.app.Service
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.practicum.playlistmaker.player.ui.fragment.PlayerFragment.Companion.TRACK_PREVIEW_URL
import com.practicum.playlistmaker.player.ui.models.PlayerStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class AudioPlayerService : Service() {

    companion object {
        private const val TIMER_UPD = 300L
    }

    private val binder = AudioPlayerServiceBinder()

    //Вместо LiveData в AudioPlayerViewModel
    private val _playerState = MutableStateFlow<PlayerStatus>(PlayerStatus.Default)
    val playerState = _playerState.asStateFlow()

    //private var track: Track? = null
    private var previewUrl: String = ""

    private var mediaPlayer: MediaPlayer? = null

    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder? {

        previewUrl = intent?.getStringExtra(TRACK_PREVIEW_URL) ?: ""

        preparePlayer()

        return binder

    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    inner class AudioPlayerServiceBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    fun playbackControl() {
        when (_playerState.value) {
            is PlayerStatus.Playing -> {
                pausePlayer()
            }

            is PlayerStatus.Prepared, PlayerStatus.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerStatus.Paused
    }

    fun releasePlayer() {
        timerJob?.cancel()
        mediaPlayer?.stop()
        _playerState.value = PlayerStatus.Default
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun preparePlayer() {
        if (previewUrl.isEmpty()) return

        mediaPlayer?.setDataSource(previewUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerStatus.Prepared(false)
        }
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.seekTo(0)
            _playerState.value = PlayerStatus.Prepared(true)
        }
    }

    fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerStatus.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(TIMER_UPD)
                _playerState.value = PlayerStatus.Playing(getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition)
            ?: "00:00"
    }

}