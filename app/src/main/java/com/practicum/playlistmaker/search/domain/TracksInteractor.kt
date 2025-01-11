package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface TracksInteractor {
    fun searchTracks(searchType: String, expression: String, consumer: TracksConsumer)

    fun getTracksHistory(consumer: TracksConsumer)
    fun clearTrackHistory()
    fun saveTracksToHistory(arrayListTracks: ArrayList<Track>)

//    fun getNightTheme(): Boolean
//    fun checkDarkTheme(): Boolean
//    fun changeDarkTheme(bool: Boolean)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }


}