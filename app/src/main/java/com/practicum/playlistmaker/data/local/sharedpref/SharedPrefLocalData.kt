package com.practicum.playlistmaker.data.local.sharedpref

import com.google.gson.Gson
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.data.LocalData
import com.practicum.playlistmaker.data.dto.TrackDto


const val TRACKHISTORY = "track_history"
const val NIGHTTHEME = "night_theme"

class SharedPrefLocalData : LocalData {

    private val sharedPref = Creator.provideSharedPreferences()

    override fun changeDarkTheme(bool: Boolean) {
        sharedPref.edit()
            .putBoolean(NIGHTTHEME, bool)
            .apply()
    }

    override fun getDarkTheme(): Boolean {
        return sharedPref.getBoolean(NIGHTTHEME, false)
    }

    override fun checkDarkTheme(): Boolean {
        return sharedPref.contains(NIGHTTHEME)
    }

    override fun getTracksHistory(): Array<TrackDto> {
        val json = sharedPref.getString(TRACKHISTORY, "")
        return try {
            Gson().fromJson(json, Array<TrackDto>::class.java)
        } catch (e: NullPointerException){
            arrayOf()
        }
    }

    override fun clearTrackHistory() {
        sharedPref.edit()
            .putString(com.practicum.playlistmaker.data.impl.TRACKHISTORY, "")
            .apply()
    }

    override fun saveTrackToHistory(arrayTrackDto: Array<TrackDto>) {
        val json = Gson().toJson(arrayTrackDto)
        sharedPref.edit()
            .putString(TRACKHISTORY, json)
            .apply()
    }

}