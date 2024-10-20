package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import java.security.AccessController.getContext


const val SETTINGS = "playListMaker_settings"
const val NIGHTTHEME = "night_theme"


class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        if (getSharedPreferences(SETTINGS, MODE_PRIVATE).contains(NIGHTTHEME)) {
            switchTheme(
                getSharedPreferences(SETTINGS, MODE_PRIVATE)
                    .getBoolean(NIGHTTHEME, false)
            )
        } else {
            when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_NO -> darkTheme = false
                Configuration.UI_MODE_NIGHT_YES -> darkTheme = true
                Configuration.UI_MODE_NIGHT_UNDEFINED -> darkTheme = false
            }
        }

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