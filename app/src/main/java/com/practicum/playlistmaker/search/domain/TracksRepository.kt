package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(searchType: String, expression: String): Flow<Resource<List<Track>>>

    fun getHistoryTracks(): Flow<Resource<List<Track>>>
    fun clearTrackHistory()
    fun saveTrackToHistory(arrayListTracks: ArrayList<Track>)


}