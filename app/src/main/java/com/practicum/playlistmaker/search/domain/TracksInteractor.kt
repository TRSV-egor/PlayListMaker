package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(searchType: String, expression: String): Flow<Pair<List<Track>?, String?>>

    fun getTracksHistory(): Flow<Pair<List<Track>?, String?>>
    fun clearTrackHistory()
    fun saveTracksToHistory(arrayListTracks: ArrayList<Track>)

}