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
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun add(playlist: PlaylistModel) {
        appDatabase.playlistDao().add(convertToPlaylistEntity(playlist))
    }

    override suspend fun remove(playlist: PlaylistModel) {
        appDatabase.playlistDao().remove(convertToPlaylistEntity(playlist))
    }

    override suspend fun update(playlist: PlaylistModel) {
        appDatabase.playlistDao().update(convertToPlaylistEntity(playlist))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<PlaylistModel> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertToPlaylistEntity(playlist: PlaylistModel): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }


}