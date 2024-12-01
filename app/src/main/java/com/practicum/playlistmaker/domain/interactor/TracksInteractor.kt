package com.practicum.playlistmaker.domain.interactor

import com.practicum.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(searchType: String, expression: String, consumer: TracksConsumer)

    fun getTracksHistory(consumer: TracksConsumer)

    fun saveTrackToHistory(track: Track)

    fun clearTrackHistory()

    fun getNightTheme(): Boolean

    fun checkDarkTheme(): Boolean

    fun changeDarkTheme(bool: Boolean)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }


}