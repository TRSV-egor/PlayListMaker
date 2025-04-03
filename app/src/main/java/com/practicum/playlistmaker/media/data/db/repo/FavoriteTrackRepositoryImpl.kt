package com.practicum.playlistmaker.media.data.db.repo

import com.practicum.playlistmaker.media.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.data.db.entity.TrackEntity
import com.practicum.playlistmaker.media.domain.db.interfaces.FavoriteTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTrackRepository {

    override fun getAll(): Flow<List<Track>> = flow {
        val favTracks = appDatabase.favoriteTrackDao().getAll()
        emit(convertFromTrackEntity(favTracks))
    }

    override suspend fun add(track: Track) {
        appDatabase.favoriteTrackDao().add(convertToTrackEntity(track))
    }

    override suspend fun remove(track: Track) {
        appDatabase.favoriteTrackDao().remove(convertToTrackEntity(track))
    }

    override suspend fun contains(track: Track): Boolean {
        return appDatabase.favoriteTrackDao()?.contains(track.trackId) != null
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }


}