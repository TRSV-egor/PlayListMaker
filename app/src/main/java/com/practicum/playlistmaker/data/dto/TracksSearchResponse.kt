package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName


class TracksSearchResponse(
    //val searchType: String,
    //val expression: String,
    @SerializedName("results") val tracksList: ArrayList<TrackDto>) : Response()