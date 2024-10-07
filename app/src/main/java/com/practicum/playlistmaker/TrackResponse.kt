package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

class TrackResponse(@SerializedName("results") val tracksList: ArrayList<Track>)