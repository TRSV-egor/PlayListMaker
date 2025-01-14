package com.practicum.playlistmaker.player.data.impl

import com.practicum.playlistmaker.player.data.MediaPlayerClient
import com.practicum.playlistmaker.player.domain.AudioPlayerRepository

class AudioPlayerRepositoryImpl(private val mediaPlayerClient: MediaPlayerClient) :
    AudioPlayerRepository {
    override fun prepare(url: String, onPrepared: () -> Unit) {
        mediaPlayerClient.prepare(url, onPrepared)
    }

    override fun pause() {
        mediaPlayerClient.pause()
    }

    override fun release() {
        mediaPlayerClient.release()
    }

    override fun start() {
        mediaPlayerClient.start()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerClient.getCurrentPosition()
    }

}