package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun checkThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}