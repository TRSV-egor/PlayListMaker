package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val SETTINGS = "playListMaker_settings"
const val NIGHTTHEME = "night_theme"


class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        switchTheme(
            getSharedPreferences(SETTINGS, MODE_PRIVATE)
                .getBoolean(NIGHTTHEME, false)
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
            .putBoolean(NIGHTTHEME, darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


}