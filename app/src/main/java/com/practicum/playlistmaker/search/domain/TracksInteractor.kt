package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(searchType: String, expression: String, consumer: TracksConsumer)

    fun getTracksHistory(consumer: TracksConsumer)
    fun clearTrackHistory()
    fun saveTracksToHistory(arrayListTracks: ArrayList<Track>)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }


}