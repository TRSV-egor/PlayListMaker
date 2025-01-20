package com.practicum.playlistmaker.util

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> darkTheme = false
            Configuration.UI_MODE_NIGHT_YES -> darkTheme = true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> darkTheme = false
        }

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


}