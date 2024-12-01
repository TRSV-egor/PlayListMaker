package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName


class TracksSearchResponse(
    @SerializedName("results") val tracksList: ArrayList<TrackDto>) : Response()