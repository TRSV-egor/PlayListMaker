package com.practicum.playlistmaker.media.domain.model

import android.os.Parcelable
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistModel(
    val id: Long,
    val name: String,
    val description: String,
    val path: String,
    val tracks: List<Track>,
) : Parcelable