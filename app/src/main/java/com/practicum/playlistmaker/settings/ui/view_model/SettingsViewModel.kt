package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.settings.ui.model.ReceivedIntent
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {


    private val darkThemeLiveMutable = MutableLiveData<Boolean>()
    val darkThemeLive: LiveData<Boolean> = darkThemeLiveMutable

    private val intentLiveDataMutable = MutableLiveData<ReceivedIntent>()
    val intentLiveData: LiveData<ReceivedIntent> = intentLiveDataMutable

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

    fun share(courseUrl: String) {
        intentLiveDataMutable.value = ReceivedIntent(sharingInteractor.shareApp(courseUrl), false)
    }

    fun support(subject: String, text: String, mail: String) {
        intentLiveDataMutable.value = ReceivedIntent(
            sharingInteractor.openSupport(
                subject, text, mail
            ), false
        )
    }

    fun agreement(practicumOffer: String) {
        intentLiveDataMutable.value =
            ReceivedIntent(sharingInteractor.openTerms(practicumOffer), false)
    }

    fun changeIntentStatus() {
        (intentLiveDataMutable.value as ReceivedIntent).isLaunched = true
    }

}