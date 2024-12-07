package com.practicum.playlistmaker.domain.models

import java.io.Serializable

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
) : Serializable
