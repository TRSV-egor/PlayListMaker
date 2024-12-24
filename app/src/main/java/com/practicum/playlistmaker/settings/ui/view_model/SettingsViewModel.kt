package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    val application = getApplication<Application>() as App

    fun darkThemeEnabled(): Boolean{
        return application.darkTheme
    }

    fun toggleTheme(isChecked: Boolean): Boolean {

        application.switchTheme(isChecked)

        return application.darkTheme

    }

}