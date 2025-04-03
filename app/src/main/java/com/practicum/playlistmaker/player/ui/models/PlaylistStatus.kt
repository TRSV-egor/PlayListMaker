package com.practicum.playlistmaker.player.ui.models

sealed interface PlaylistStatus {
    data class Added(val notify: Boolean, val message: String) : PlaylistStatus
    data class Exist(val notify: Boolean, val message: String) : PlaylistStatus
    data class Default(val notify: Boolean, val message: String) : PlaylistStatus
}