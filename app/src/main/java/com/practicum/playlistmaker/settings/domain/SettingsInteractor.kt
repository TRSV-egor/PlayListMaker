package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}