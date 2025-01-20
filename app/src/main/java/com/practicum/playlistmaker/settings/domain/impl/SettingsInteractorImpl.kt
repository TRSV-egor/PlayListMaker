package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(
    private val repository: SettingsRepository,
) : SettingsInteractor {

    override fun updateThemeSettings(settings: ThemeSettings) {
        repository.changeDarkTheme(settings)
    }

    override fun checkThemeSettings(): Boolean {
        return repository.checkRecordDarkTheme()
    }

    override fun getDarkTheme(): ThemeSettings {
        return repository.getDarkTheme()
    }


}