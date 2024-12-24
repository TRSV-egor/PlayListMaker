package com.practicum.playlistmaker.player.ui.model

import com.practicum.playlistmaker.PlayerState

data class AudioplayerLiveData(
    var buttonPlayIsEnabled: Boolean,
    var buttonPlayBackground: Boolean,
    var trackTimer: String,
    var playerState: PlayerState,
)