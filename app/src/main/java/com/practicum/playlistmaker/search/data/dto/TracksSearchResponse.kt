package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName


class TracksSearchResponse(
    @SerializedName("results") val tracksList: List<TrackDto>
) : Response()