package com.practicum.playlistmaker.data.local.sharedpref

import com.google.gson.Gson
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.data.LocalData
import com.practicum.playlistmaker.data.dto.TrackDto


const val TRACKHISTORY = "track_history"
const val NIGHTTHEME = "night_theme"

class SharedPrefLocalData : LocalData {

    val sharedPref = Creator.provideSharedPreferences()

    var sharedTrackHistoryArrayList: ArrayList<TrackDto> = arrayListOf()
    val maxSizesharedTrackHistory: Int = 10

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

    override fun getTracksHistory(): ArrayList<TrackDto> {
            updateSharedTrackHistoryArrayList()
           return sharedTrackHistoryArrayList
    }

    override fun clearTrackHistory() {
            clearTrackArrayAndShared()
    }

    override fun saveTrackToHistory(trackDto: TrackDto) {
        saveTrackToArray(trackDto)
    }



    fun updateSharedTrackHistoryArrayList() {
        val json = sharedPref.getString(TRACKHISTORY, "")
        sharedTrackHistoryArrayList.clear()

        try {
            sharedTrackHistoryArrayList.addAll(Gson().fromJson(json, Array<TrackDto>::class.java))
        } catch (e: NullPointerException) { }
    }

    fun clearTrackArrayAndShared() {
        sharedPref.edit()
            .putString(com.practicum.playlistmaker.data.impl.TRACKHISTORY, "")
            .apply()
        sharedTrackHistoryArrayList.clear()
    }

    fun saveTrackToArray(trackDto: TrackDto) {

        if (sharedTrackHistoryArrayList.contains(trackDto)) {
            sharedTrackHistoryArrayList.remove(trackDto)
        }

        sharedTrackHistoryArrayList.add(0, trackDto)

        if (sharedTrackHistoryArrayList.size > maxSizesharedTrackHistory) {
            sharedTrackHistoryArrayList.removeAt(maxSizesharedTrackHistory)
        }

        updateTracksInSharedPref()
    }

    fun updateTracksInSharedPref(){
        val json = Gson().toJson(sharedTrackHistoryArrayList)
        sharedPref.edit()
            .putString(TRACKHISTORY, json)
            .apply()
    }


}