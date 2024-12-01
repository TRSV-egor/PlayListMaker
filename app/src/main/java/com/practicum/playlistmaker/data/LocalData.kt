package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackDto

interface LocalData {
    fun getTracksHistory(): Array<TrackDto>
    fun clearTrackHistory()
    fun saveTrackToHistory(arrayTrackDto: Array<TrackDto>)

    fun getDarkTheme(): Boolean
    fun checkDarkTheme(): Boolean
    fun changeDarkTheme(bool: Boolean)
}