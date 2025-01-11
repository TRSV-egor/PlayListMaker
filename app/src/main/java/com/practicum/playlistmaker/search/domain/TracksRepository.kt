package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {
    fun searchTracks(searchType: String, expression: String): Resource<List<Track>>

    fun getHistoryTracks(): Resource<List<Track>>
    fun clearTrackHistory()
    fun saveTrackToHistory(arrayListTracks: ArrayList<Track>)


}