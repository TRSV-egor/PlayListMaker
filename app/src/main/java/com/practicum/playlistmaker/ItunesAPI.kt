package com.practicum.playlistmaker


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {

    @GET("/search")
    fun getTrack(
        @Query("entity") entity: String,
        @Query("term") query: String,
    ): Call<TrackResponse>

}