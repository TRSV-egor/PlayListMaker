package com.practicum.playlistmaker.data.impl

import com.practicum.playlistmaker.data.LocalData
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repositories.TracksRepository
import java.text.SimpleDateFormat
import java.util.Locale

const val TRACKHISTORY = "track_history"

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localData: LocalData
) : TracksRepository {

    override fun searchTracks(searchType: String, expression: String): List<Track>? {
        val networkClientResponse =
            networkClient.doRequest(TracksSearchRequest(searchType, expression))
        when (networkClientResponse.resultCode) {
            200 -> {
                return (networkClientResponse as TracksSearchResponse).tracksList.map {
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
                }
            }
            408 -> {
                return null
            }
            else -> {
                return emptyList()
            }
        }
    }

    override fun getHistoryTracks(): ArrayList<Track> {

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

        return arrayList
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

    override fun getNightTheme(): Boolean {
        return localData.getDarkTheme()
    }

    override fun checkDarkTheme(): Boolean {
        return localData.checkDarkTheme()
    }

    override fun changeDarkTheme(bool: Boolean) {
        localData.changeDarkTheme(bool)
    }

    private fun convertDateToFormat(time: String): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time.toLongOrNull())
    }

}

