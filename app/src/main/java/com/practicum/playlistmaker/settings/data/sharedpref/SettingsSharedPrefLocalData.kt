package com.practicum.playlistmaker.settings.data.sharedpref

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.data.SettingsLocalData


class SettingsSharedPrefLocalData(private val sharedPref: SharedPreferences) : SettingsLocalData {

    companion object {
        const val NIGHT_THEME = "night_theme"
    }

    override fun changeDarkTheme(bool: Boolean) {
        sharedPref.edit()
            .putBoolean(NIGHT_THEME, bool)
            .apply()
    }

    override fun checkRecordDarkTheme(): Boolean {
        return sharedPref.contains(NIGHT_THEME)
    }

    override fun getDarkTheme(): Boolean {
        return sharedPref.getBoolean(NIGHT_THEME, false)
    }

}