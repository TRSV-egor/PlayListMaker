package com.practicum.playlistmaker.player.domain

interface AudioPlayerRepository {
    fun prepare(url: String, onPrepared: (Boolean) -> Unit)
    fun release()
    fun pause()
    fun start()
    fun getCurrentPosition(): Int
}