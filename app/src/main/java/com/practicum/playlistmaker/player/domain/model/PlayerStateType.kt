package com.practicum.playlistmaker.player.domain.model

enum class PlayerStateType(state: Int) {
    DEFAULT(0),
    PREPARED(1),
    PLAYING(2),
    PAUSED(3),
}