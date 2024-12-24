package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    companion object {

        const val MAIL_TO = "mailto:"

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    Creator.provideSettingsInteractor(),
                    Creator.provideSharingInteractor()
                )
            }
        }
    }

    private val darkThemeLiveMutable = MutableLiveData<Boolean>()
    val darkThemeLive: LiveData<Boolean> = darkThemeLiveMutable

    fun checkDarkTheme() {
        darkThemeLiveMutable.value = settingsInteractor.checkThemeSettings().darkTheme
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