package com.practicum.playlistmaker.di

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.LocalData
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.network.ItunesAPI
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.sharedpref.SharedPrefLocalData
import com.practicum.playlistmaker.settings.data.SettingsLocalData
import com.practicum.playlistmaker.settings.data.sharedpref.SettingsSharedPrefLocalData
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val SETTINGS = "playListMaker_settings"

val dataModule = module {

    single<ExecutorService> {
        Executors.newCachedThreadPool()
    }

    factory<MediaPlayer>{
        MediaPlayer()
    }

    single<NetworkClient>{
        RetrofitNetworkClient(get())
    }

    single<ItunesAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPI::class.java)
    }

    single<SharedPreferences>{
        androidContext().getSharedPreferences(SETTINGS, MODE_PRIVATE)
    }

    single<LocalData>{
        SharedPrefLocalData(sharedPref = get())
    }

    single<SettingsLocalData>{
        SettingsSharedPrefLocalData(sharedPref = get())
    }

    single {
        Room.databaseBuilder(
            name = "database.db",
            klass = AppDatabase::class.java,
            context = androidContext()
        )
            .fallbackToDestructiveMigration()
            .build()
    }

}