package com.practicum.playlistmaker.player.data.media_player

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.MediaPlayerClient

class MediaPlayerClientImpl : MediaPlayerClient {

    private var mediaPlayer = MediaPlayer()

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun prepare(url: String, onPrepared: () -> Unit) {
        mediaPlayer.apply {
            setDataSource(url)
            setOnPreparedListener {
                onPrepared()
            }
            prepareAsync()
            setOnCompletionListener {
                onPrepared()
            }
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

}