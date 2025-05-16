package com.practicum.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
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

    //region Переменные
    companion object {
        private const val TIMER_UPD = 300L
        const val SERVICE_NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
    }

    private val binder = AudioPlayerServiceBinder()

    private val _playerState = MutableStateFlow<PlayerStatus>(PlayerStatus.Default)
    val playerState = _playerState.asStateFlow()

    private var previewUrl: String = ""

    private var mediaPlayer: MediaPlayer? = null

    private var timerJob: Job? = null

    //endregion

    //region Жизненный цикл
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder {
        previewUrl = intent?.getStringExtra(TRACK_PREVIEW_URL) ?: ""
        preparePlayer()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }
    //endregion

    //region Служебные
    inner class AudioPlayerServiceBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }
    //endregion

    //region Управление плеером
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

    fun getStatus(): PlayerStatus {
        return playerState.value
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
            timerJob?.cancel()
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
            while (_playerState.value is PlayerStatus.Playing) {
                delay(TIMER_UPD)
                _playerState.value = PlayerStatus.Playing(getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition)
            ?: "00:00"
    }
    //endregion

    //region Notification
    fun startNotification(description: String) {
        ServiceCompat.startForeground(
            /* service = */ this,
            /* id = */ SERVICE_NOTIFICATION_ID,
            /* notification = */ createServiceNotification(description),
            /* foregroundServiceType = */ getForegroundServiceTypeConstant()
        )
    }

    fun stopNotification() {
        ServiceCompat.stopForeground(
            /* service = */ this, ServiceCompat.STOP_FOREGROUND_REMOVE,
        )
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            /* id= */ NOTIFICATION_CHANNEL_ID,
            /* name= */ "Audioplayer",
            /* importance= */ NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(description: String): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("PlaylistMaker")
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
    }
    //endregion
}


