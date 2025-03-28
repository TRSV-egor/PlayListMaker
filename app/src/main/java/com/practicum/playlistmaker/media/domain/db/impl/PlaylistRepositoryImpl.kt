package com.practicum.playlistmaker.media.domain.db.impl

import com.practicum.playlistmaker.media.data.converters.PlaylistDBConvertor
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDBConvertor
) : PlaylistRepository {

    override fun getAll(): Flow<List<PlaylistModel>> = flow {
        val playlists = appDatabase.playlistDao().getAll()
        emit(convertFromTrackEntity(playlists))
    }

    override suspend fun add(playlist: PlaylistModel) {
        appDatabase.playlistDao().add(convertToTrackEntity(playlist))
    }

    override suspend fun remove(playlist: PlaylistModel) {
        appDatabase.playlistDao().remove(convertToTrackEntity(playlist))
    }

    override suspend fun update(playlist: PlaylistModel) {
        appDatabase.playlistDao().update(convertToTrackEntity(playlist))
    }

    private fun convertFromTrackEntity(playlists: List<PlaylistEntity>): List<PlaylistModel> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertToTrackEntity(playlist: PlaylistModel): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }


}