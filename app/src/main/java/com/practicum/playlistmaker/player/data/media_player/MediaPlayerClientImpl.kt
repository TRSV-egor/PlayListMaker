package com.practicum.playlistmaker.player.data.media_player

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.MediaPlayerClient

class MediaPlayerClientImpl(
    private var mediaPlayer: MediaPlayer?
) : MediaPlayerClient {

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        //Обход ошибки вылета плеера при возвращении с экрана создания плейлиста
        mediaPlayer = null
    }

    override fun prepare(url: String, onPrepared: (Boolean) -> Unit) {
        //Обход ошибки неработающего плеера при возвращении с экрана создания плейлиста
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer?.apply {
            setDataSource(url)
            setOnPreparedListener {
                onPrepared(false)
            }
            prepareAsync()
            setOnCompletionListener {
                mediaPlayer?.seekTo(0)
                onPrepared(true)
            }

        }
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer!!.currentPosition
    }

}