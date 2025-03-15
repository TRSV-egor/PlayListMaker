package com.practicum.playlistmaker.search.data.network


import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {

    @GET("/search")
    suspend fun getTracks(
        @Query("entity") entity: String,
        @Query("term") query: String,
    ): TracksSearchResponse

}