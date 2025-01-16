package com.practicum.playlistmaker.player.domain

interface AudioPlayerInteractor {
    fun prepare(url: String, onPrepared: (Boolean) -> Unit)
    fun release()
    fun pause()
    fun start()
    fun getCurrentPosition(): Int
}