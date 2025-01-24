package com.practicum.playlistmaker.player.data.media_player

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.MediaPlayerClient

class MediaPlayerClientImpl(
    private var mediaPlayer: MediaPlayer
) : MediaPlayerClient {

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun prepare(url: String, onPrepared: (Boolean) -> Unit) {
        mediaPlayer.apply {
            setDataSource(url)
            setOnPreparedListener {
                onPrepared(false)
            }
            prepareAsync()
            setOnCompletionListener {
                onPrepared(true)
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