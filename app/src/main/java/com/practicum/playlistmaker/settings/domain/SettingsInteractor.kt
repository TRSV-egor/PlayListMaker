package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun checkThemeSettings(): Boolean
    fun updateThemeSettings(settings: ThemeSettings)
    fun getDarkTheme(): ThemeSettings
}