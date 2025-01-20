package com.practicum.playlistmaker.settings.data

interface SettingsLocalData {
    fun getDarkTheme(): Boolean
    fun checkRecordDarkTheme(): Boolean
    fun changeDarkTheme(bool: Boolean)
}