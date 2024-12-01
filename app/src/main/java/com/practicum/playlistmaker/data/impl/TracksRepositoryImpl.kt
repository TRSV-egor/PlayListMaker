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

    override fun searchTracks(searchType: String, expression: String): List<Track> {
        val resp = networkClient.doRequest(TracksSearchRequest(searchType, expression))
        if (resp.resultCode == 200) {
            return trackDtoToTrack((resp as TracksSearchResponse).tracksList)
        } else {
            return emptyList()
        }
    }

    override fun changeDarkTheme(bool: Boolean) {
        localData.changeDarkTheme(bool)
    }

    override fun getNightTheme(): Boolean {
        return localData.getDarkTheme()
    }

    override fun saveTrackToHistory(track: Track) {

        localData.saveTrackToHistory(
            TrackDto(
                track.trackId ?: "",
                track.trackName ?: "", // Название композиции
                track.artistName ?: "", // Имя исполнителя
                convertDatetoLong(track.trackTime), // Продолжительность трека
                track.artworkUrl100 ?: "", // Ссылка на изображение обложки
                track.releaseDate ?: "",
                track.primaryGenreName ?: "",
                track.country ?: "",
                track.collectionName ?: "",
                track.previewUrl ?: "",
            )
        )
    }

    override fun clearTrackHistory() {
        localData.clearTrackHistory()
    }

    override fun getHistoryTracks(): List<Track> {
        return trackDtoToTrack(
            localData.getTracksHistory()
        )

    }

    override fun checkDarkTheme(): Boolean {
       return localData.checkDarkTheme()
    }


    private fun trackDtoToTrack(list: List<TrackDto>): List<Track> {
        return list.map {
            with(it) {
                Track(
                    trackId ?: "",
                    trackName ?: "", // Название композиции
                    artistName ?: "", // Имя исполнителя
                    convertDateToFormat(trackTime) ?: "", // Продолжительность трека
                    artworkUrl100 ?: "", // Ссылка на изображение обложки
                    releaseDate ?: "",
                    primaryGenreName ?: "",
                    country ?: "",
                    collectionName ?: "",
                    previewUrl ?: "",
                )
            }
        }
    }

    private fun trackToTrackDto(list: List<Track>): List<TrackDto> {
        return list.map {
            with(it) {
                TrackDto(
                    trackId ?: "",
                    trackName ?: "", // Название композиции
                    artistName ?: "", // Имя исполнителя
                    convertDatetoLong(trackTime), // Продолжительность трека
                    artworkUrl100 ?: "", // Ссылка на изображение обложки
                    releaseDate ?: "",
                    primaryGenreName ?: "",
                    country ?: "",
                    collectionName ?: "",
                    previewUrl ?: "",
                )
            }
        }
    }

    fun convertDateToFormat(time: String): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time.toLongOrNull())
    }

    fun convertDatetoLong(time: String): String {

        var result: Long = 0L

        val parts = time.split(":")

        val minutes = parts[0].toLongOrNull() ?: 0L
        val seconds = parts[1].toLongOrNull() ?: 0L

        result = minutes * 60L + seconds
        return result.toString()

    }
}

