package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    companion object {
        const val MAIL_TO = "mailto:"
    }

    private val darkThemeLiveMutable = MutableLiveData<Boolean>()
    val darkThemeLive: LiveData<Boolean> = darkThemeLiveMutable

    fun checkDarkTheme(systemDarkTheme: Boolean) {
        if (settingsInteractor.checkThemeSettings()) {
            darkThemeLiveMutable.value = settingsInteractor.getDarkTheme().darkTheme
        } else {
            darkThemeLiveMutable.value = systemDarkTheme
        }

    }

    fun toggleTheme(isChecked: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeSettings(isChecked))
    }

    fun share() {
        sharingInteractor.shareApp()
    }

    fun support() {
        sharingInteractor.openSupport()
    }

    fun agreement() {
        sharingInteractor.openTerms()
    }


}