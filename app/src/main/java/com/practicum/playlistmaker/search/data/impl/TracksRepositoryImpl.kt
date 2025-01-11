package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.data.LocalData
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import java.text.SimpleDateFormat
import java.util.Locale

const val TRACKHISTORY = "track_history"

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localData: LocalData
) : TracksRepository {

    override fun searchTracks(searchType: String, expression: String): Resource<List<Track>> {
        val networkClientResponse =
            networkClient.doRequest(TracksSearchRequest(searchType, expression))
        when (networkClientResponse.resultCode) {
            200 -> {
                return Resource.Success((networkClientResponse as TracksSearchResponse).tracksList.map {
                    with(it) {
                        Track(
                            trackId ,
                            trackName,
                            artistName,
                            convertDateToFormat(trackTime),
                            artworkUrl100,
                            releaseDate,
                            primaryGenreName,
                            country,
                            collectionName,
                            previewUrl,
                        )
                    }
                })

            }
            -1 -> {
                return Resource.Error("Проверьте подключение к интернету")
            }
            else -> {
                return  Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun getHistoryTracks(): Resource<List<Track>> {

        val arrayList: ArrayList<Track> = arrayListOf()

        arrayList.addAll(localData.getTracksHistory().map {
            with(it) {
                Track(
                    trackId,
                    trackName,
                    artistName,
                    trackTime,
                    artworkUrl100,
                    releaseDate,
                    primaryGenreName,
                    country,
                    collectionName,
                    previewUrl,
                )
            }
        })

        return Resource.Success(arrayList)
    }

    override fun clearTrackHistory() {
        localData.clearTrackHistory()
    }

    override fun saveTrackToHistory(arrayListTracks: ArrayList<Track>) {
        localData.saveTrackToHistory(
            arrayListTracks.map {
                TrackDto(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTime,
                    it.artworkUrl100,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.collectionName,
                    it.previewUrl,
                )
            }.toTypedArray()
        )
    }

    private fun convertDateToFormat(time: String): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time.toLongOrNull())
    }

}

