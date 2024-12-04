package com.practicum.playlistmaker.data.network


import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {

    @GET("/search")
    fun getTracks(
        @Query("entity") entity: String,
        @Query("term") query: String,
    ): Call<TracksSearchResponse>

}