package com.practicum.playlistmaker.player.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface PlayerStatus {

    data class Default(val track: Track) : PlayerStatus
    data object Prepared : PlayerStatus
    data object Paused : PlayerStatus
    data object Playing : PlayerStatus {
        var timer: String = "00:00"
    }

}