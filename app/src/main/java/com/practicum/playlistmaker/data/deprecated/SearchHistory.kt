package com.practicum.playlistmaker.data.deprecated//package com.practicum.playlistmaker.data.deprecated
//
//import android.content.SharedPreferences
//import com.google.gson.Gson
//import com.practicum.playlistmaker.data.impl.TRACKHISTORY
//import com.practicum.playlistmaker.domain.models.Track
//
////Перенесено
////const val TRACKHISTORY = "track_history"
//
//class SearchHistory(private val sharedPreferences: SharedPreferences) {
//
//    var sharedTrackHistory: ArrayList<Track> = arrayListOf()
//
//    val maxSavedTracks: Int = 10
//
//    fun update() {
//        val json = sharedPreferences.getString(TRACKHISTORY, "")
//        sharedTrackHistory.clear()
//        //Добавить обработчик проверки на null
//        try {
//            sharedTrackHistory.addAll(Gson().fromJson(json, Array<Track>::class.java))
//        } catch (e: NullPointerException) {
//
//        }
//
//    }
//
//    fun get(): ArrayList<Track> {
//        update()
//        return sharedTrackHistory
//    }
//
//    fun count(): Int {
//        return sharedTrackHistory.size
//    }
//
//    fun save(track: Track) {
//
//        if (sharedTrackHistory.contains(track)) {
//            sharedTrackHistory.remove(track)
//        }
//
//        sharedTrackHistory.add(0, track)
//
//        if (sharedTrackHistory.size > maxSavedTracks) {
//            sharedTrackHistory.removeAt(maxSavedTracks)
//        }
//
//        val json = Gson().toJson(sharedTrackHistory)
//        sharedPreferences.edit()
//            .putString(TRACKHISTORY, json)
//            .apply()
//    }
//
//    fun delete(track: Track) {
//        if (sharedTrackHistory.contains(track)) {
//            sharedTrackHistory.remove(track)
//        }
//    }
//
//    fun clear() {
//        sharedPreferences.edit()
//            .putString(TRACKHISTORY, "")
//            .apply()
//        sharedTrackHistory.clear()
//    }
//
//}