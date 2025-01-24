package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.settings.ui.model.EmailData
import com.practicum.playlistmaker.settings.ui.model.IntentType

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val darkThemeLiveMutable = MutableLiveData<Boolean>()
    val darkThemeLive: LiveData<Boolean> = darkThemeLiveMutable

    private val intentLiveDataMutable = MutableLiveData<IntentType>()
    val intentLiveData: LiveData<IntentType> = intentLiveDataMutable

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

    fun shareApp(courseUrl: String) {
        intentLiveDataMutable.value = IntentType.ShareApp(courseUrl)
    }

    fun getHelp(emailData: EmailData) {
        intentLiveDataMutable.value = IntentType.GetHelp(emailData)
    }

    fun userAgreement(practicumOffer: String) {
        intentLiveDataMutable.value = IntentType.UserAgreement(practicumOffer)
    }

    fun changeIntentStatus() {
        (intentLiveDataMutable.value as IntentType).isLaunched = true
    }

}