package com.practicum.playlistmaker

enum class PlayerState(val state: Int) {
    DEFAULT(0),
    PREPARED(1),
    PLAYING(2),
    PAUSED(3),
}