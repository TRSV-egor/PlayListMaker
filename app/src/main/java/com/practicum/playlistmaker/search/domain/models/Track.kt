package com.practicum.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val collectionName: String,
    val previewUrl: String,
) : Parcelable
