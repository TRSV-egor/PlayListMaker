package com.practicum.playlistmaker.presentation.ui

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.creator.Creator.provideTracksInteractor

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)
        val tracksInteractor = provideTracksInteractor()
        if (tracksInteractor.checkDarkTheme()) {
            switchTheme(
                tracksInteractor.getNightTheme()
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
        val tracksInteractor = provideTracksInteractor()
        tracksInteractor.changeDarkTheme(darkThemeEnabled)

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


}