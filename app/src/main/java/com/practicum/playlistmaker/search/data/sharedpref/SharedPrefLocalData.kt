package com.practicum.playlistmaker.search.data.sharedpref

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.LocalData
import com.practicum.playlistmaker.search.data.dto.TrackDto



class SharedPrefLocalData(private val sharedPref: SharedPreferences) : LocalData {

    companion object {
        const val TRACK_HISTORY = "track_history"
    }

    override fun getTracksHistory(): Array<TrackDto> {
        val json = sharedPref.getString(TRACK_HISTORY, "")
        return try {
            Gson().fromJson(json, Array<TrackDto>::class.java)
        } catch (e: NullPointerException){
            arrayOf()
        }
    }

    override fun clearTrackHistory() {
        sharedPref.edit()
            .putString(com.practicum.playlistmaker.search.data.impl.TRACKHISTORY, "")
            .apply()
    }

    override fun saveTrackToHistory(arrayTrackDto: Array<TrackDto>) {
        val json = Gson().toJson(arrayTrackDto)
        sharedPref.edit()
            .putString(TRACK_HISTORY, json)
            .apply()
    }

}