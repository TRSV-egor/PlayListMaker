package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.sharedpref.SharedPrefLocalData
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksRepository

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