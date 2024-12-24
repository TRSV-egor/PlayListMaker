package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TrackDto

interface LocalData {
    fun getTracksHistory(): Array<TrackDto>
    fun clearTrackHistory()
    fun saveTrackToHistory(arrayTrackDto: Array<TrackDto>)

//    fun getDarkTheme(): Boolean
//    fun checkDarkTheme(): Boolean
//    fun changeDarkTheme(bool: Boolean)
}