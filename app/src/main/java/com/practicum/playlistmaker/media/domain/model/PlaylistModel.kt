package com.practicum.playlistmaker.media.domain.model

import android.net.Uri
import com.practicum.playlistmaker.search.domain.models.Track

data class PlaylistModel(

    val name: String,
    val description: String,
    val path: Uri,
    val tracks: List<Track>,

    )