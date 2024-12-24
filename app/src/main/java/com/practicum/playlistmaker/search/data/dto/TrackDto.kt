package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

data class TrackDto(
    val trackId: String,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val collectionName: String,
    val previewUrl: String,
)