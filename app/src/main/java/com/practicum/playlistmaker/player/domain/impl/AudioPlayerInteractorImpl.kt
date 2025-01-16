package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.AudioPlayerInteractor
import com.practicum.playlistmaker.player.domain.AudioPlayerRepository

class AudioPlayerInteractorImpl(private val repository: AudioPlayerRepository) :
    AudioPlayerInteractor {

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun prepare(url: String, onPrepared: (Boolean) -> Unit) {
        repository.prepare(url, onPrepared)
    }

    override fun start() {
        repository.start()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

}