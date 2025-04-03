package com.practicum.playlistmaker.util

import android.app.Application
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }

        checkTheme()
//        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
//            Configuration.UI_MODE_NIGHT_NO -> darkTheme = false
//            Configuration.UI_MODE_NIGHT_YES -> darkTheme = true
//            Configuration.UI_MODE_NIGHT_UNDEFINED -> darkTheme = false
//        }

    }

    fun checkTheme() {
        val settingsInteractor: SettingsInteractor by inject()
        val containsTheme = settingsInteractor.checkThemeSettings()
        if (containsTheme) {
            switchTheme(settingsInteractor.getDarkTheme().darkTheme)
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

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun getStringFromResources(@StringRes resId: Int): String {
        return resources.getString(resId)
    }


}