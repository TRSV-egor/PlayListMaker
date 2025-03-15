package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RetrofitNetworkClient(
    private val iTunesService: ItunesAPI
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {

        if (dto !is TracksSearchRequest) {
            return Response().apply { resultCode = 400 }
        }


        return withContext(Dispatchers.IO) {
            try {
                val resp = iTunesService.getTracks(dto.searchType, dto.expression)
                resp.apply { resultCode = 200 }
            } catch (e: Exception) {
                Response().apply { resultCode = 408 }
            }
        }



    }
}