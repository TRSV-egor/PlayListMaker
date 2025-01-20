package com.practicum.playlistmaker.settings.data

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getDarkTheme(): ThemeSettings
    fun checkRecordDarkTheme(): Boolean
    fun changeDarkTheme(bool: ThemeSettings)
}