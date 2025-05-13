package com.practicum.playlistmaker.player.service

import android.app.Service
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.fragment.SearchFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class AudioPlayerService : Service() {

    private val binder = AudioPlayerServiceBinder()

    //private val _playerState = MutableStateFlow<PlayerStatus>()
    //val playerState = _playerState.asStateFlow()

    //private var songUrl = ""
    private var track: Track? = null

    private var mediaPlayer: MediaPlayer? = null

    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder? {

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(SearchFragment.TRACK_BUNDLE, Track::class.java)
        } else {
            intent?.getParcelableExtra(SearchFragment.TRACK_BUNDLE)
        }

        initMediaPlayer()

        return binder

    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    inner class AudioPlayerServiceBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    private fun initMediaPlayer() {
        if (track?.previewUrl.isNullOrEmpty()) return

        mediaPlayer?.setDataSource(track?.previewUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            //_playerState.value = PlayerState.Prepared()
        }
        mediaPlayer?.setOnCompletionListener {
            //_playerState.value = PlayerState.Prepared()
        }
    }

    fun startPlayer() {
        mediaPlayer?.start()
        //_playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        //_playerState.value = PlayerState.Paused(getCurrentPlayerPosition())
    }

    private fun releasePlayer() {
        timerJob?.cancel()
        mediaPlayer?.stop()
        //_playerState.value = PlayerState.Default()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(200L)
                //_playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition)
            ?: "00:00"
    }

}