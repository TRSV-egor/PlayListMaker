package com.practicum.playlistmaker.domain.repositories

import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(searchType: String, expression: String): List<Track>?

    fun getHistoryTracks(): ArrayList<Track>
    fun clearTrackHistory()
    fun saveTrackToHistory(arrayListTracks: ArrayList<Track>)

    fun getNightTheme(): Boolean
    fun checkDarkTheme(): Boolean
    fun changeDarkTheme(bool: Boolean)
}