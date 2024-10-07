package com.practicum.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ItunesRetrofit {

    private val baseURL = "https://itunes.apple.com"

    fun getService(): ItunesAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ItunesAPI::class.java)
    }


}