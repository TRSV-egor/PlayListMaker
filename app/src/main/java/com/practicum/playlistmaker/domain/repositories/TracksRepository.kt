package com.practicum.playlistmaker.domain.repositories

import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(searchType: String, expression: String): List<Track>
    fun getHistoryTracks(): List<Track>
    fun clearTrackHistory()
    fun saveTrackToHistory(track: Track)
    fun getNightTheme(): Boolean
    fun checkDarkTheme(): Boolean
    fun changeDarkTheme(bool: Boolean)
}