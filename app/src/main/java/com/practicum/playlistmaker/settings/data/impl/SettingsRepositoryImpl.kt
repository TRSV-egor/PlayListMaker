package com.practicum.playlistmaker.settings.data.impl

import com.practicum.playlistmaker.settings.data.SettingsLocalData
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val settingsLocalData: SettingsLocalData
) : SettingsRepository {

    override fun getDarkTheme(): ThemeSettings {
        return ThemeSettings(settingsLocalData.getDarkTheme())
    }

    override fun checkRecordDarkTheme(): Boolean {
        return settingsLocalData.checkRecordDarkTheme()
    }

    override fun changeDarkTheme(bool: ThemeSettings) {
        settingsLocalData.changeDarkTheme(bool.darkTheme)
    }
}