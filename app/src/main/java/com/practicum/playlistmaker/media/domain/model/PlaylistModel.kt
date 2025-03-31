package com.practicum.playlistmaker.media.domain.model

import com.practicum.playlistmaker.search.domain.models.Track

data class PlaylistModel(

    val id: Long,
    val name: String,
    val description: String,
    val path: String,
    val tracks: List<Track>,

    )