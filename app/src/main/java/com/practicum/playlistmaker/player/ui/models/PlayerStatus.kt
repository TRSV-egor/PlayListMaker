package com.practicum.playlistmaker.player.ui.models

sealed interface PlayerStatus {

    companion object {
        const val ZERO_TIMER = "00:00"
    }

    data object Default : PlayerStatus

    //data class Default(val track: Track) : PlayerStatus
    data class Prepared(val isTrackCompleted: Boolean) : PlayerStatus
    data object Paused : PlayerStatus
    data class Playing(val timer: String) : PlayerStatus

}