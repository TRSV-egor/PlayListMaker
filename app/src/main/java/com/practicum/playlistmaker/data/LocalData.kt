package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackDto

interface LocalData {
    fun getTracksHistory(): ArrayList<TrackDto>
    fun clearTrackHistory()
    fun saveTrackToHistory(track: TrackDto)
    fun getDarkTheme(): Boolean
    fun checkDarkTheme(): Boolean
    fun changeDarkTheme(bool: Boolean)
}