package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest


class RetrofitNetworkClient(
    private val iTunesService: ItunesAPI
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {

            try {
                val resp = iTunesService.getTracks(dto.searchType, dto.expression).execute()
                val body = resp.body() ?: Response()
                return body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                return Response().apply { resultCode = 408 }
            }

        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}