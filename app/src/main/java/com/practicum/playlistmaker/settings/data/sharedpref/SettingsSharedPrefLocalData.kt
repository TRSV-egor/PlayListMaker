package com.practicum.playlistmaker.settings.data.sharedpref

import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.data.SettingsLocalData

const val NIGHTTHEME = "night_theme"

class SettingsSharedPrefLocalData : SettingsLocalData {

    private val sharedPref = Creator.provideSharedPreferences()

    override fun changeDarkTheme(bool: Boolean) {
        sharedPref.edit()
            .putBoolean(NIGHTTHEME, bool)
            .apply()
    }

    override fun checkRecordDarkTheme(): Boolean {
        return sharedPref.contains(NIGHTTHEME)
    }

    override fun getDarkTheme(): Boolean {
        return sharedPref.getBoolean(NIGHTTHEME, false)
    }

}