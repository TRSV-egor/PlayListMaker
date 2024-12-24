package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(searchType: String, expression: String): List<Track>?

    fun getHistoryTracks(): ArrayList<Track>
    fun clearTrackHistory()
    fun saveTrackToHistory(arrayListTracks: ArrayList<Track>)


}