package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.data.local.sharedpref.SharedPrefLocalData
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.repositories.TracksRepository

const val SETTINGS = "playListMaker_settings"

object Creator {

    //Shared Preferences
    private lateinit var application: Application

    fun initApplication(app: Application){
        this.application = app
    }

    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(SETTINGS, MODE_PRIVATE)
    }

    //Track search
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(), SharedPrefLocalData())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
    
}