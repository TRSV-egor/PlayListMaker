package com.practicum.playlistmaker.player.data

interface MediaPlayerClient {
    fun prepare(url: String, onPrepared: (Boolean) -> Unit)
    fun release()
    fun pause()
    fun start()
    fun getCurrentPosition(): Int
}