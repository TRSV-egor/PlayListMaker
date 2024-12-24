package com.practicum.playlistmaker.settings.domain.impl

import android.app.Application
import com.practicum.playlistmaker.util.App
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    private val repository: SettingsRepository,
    private val application: Application
) : SettingsInteractor {

    override fun updateThemeSettings(settings: ThemeSettings) {
        (application as App).switchTheme(settings.darkTheme)
        repository.changeDarkTheme(settings)
    }

    override fun checkThemeSettings(): ThemeSettings {
        return if (repository.checkRecordDarkTheme()) {
            repository.getDarkTheme()
        } else {
            ThemeSettings((application as App).darkTheme)
        }
    }
}