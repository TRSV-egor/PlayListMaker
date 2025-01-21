package com.practicum.playlistmaker.settings.ui.model

import android.content.Intent

data class ReceivedIntent(
    val intent: Intent,
    var isLaunched: Boolean
)
