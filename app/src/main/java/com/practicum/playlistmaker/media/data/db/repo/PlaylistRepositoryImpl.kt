package com.practicum.playlistmaker.media.data.db.repo

import com.practicum.playlistmaker.media.data.converters.PlaylistDBConvertor
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistRepository
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
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

    override suspend fun add(name: String, path: String, description: String) {

        val result = PlaylistEntity(
            name = name,
            description = description,
            path = path,
            tracks = "[]",
            count = 0,
        )
        appDatabase.playlistDao().add(result)

    }

    override suspend fun remove(playlist: PlaylistModel) {
        appDatabase.playlistDao().remove(convertToPlaylistEntity(playlist))
    }

    override suspend fun update(playlist: PlaylistModel) {
        appDatabase.playlistDao().update(convertToPlaylistEntity(playlist))
    }

    override fun findById(id: Long): Flow<PlaylistModel> = flow {
        emit(playlistDbConvertor.map(appDatabase.playlistDao().findById(id)))
    }

    override suspend fun globalTrackSearch(track: Track): List<Long> {
        var resultId = mutableListOf<Long>()
        var playlistsEntity = appDatabase.playlistDao().getAll()
        var playlistModel = convertFromPlaylistEntity(playlistsEntity)
        playlistModel.forEach {
            if (it.tracks.contains(track)) resultId.add(it.id)
        }
        return resultId
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<PlaylistModel> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertToPlaylistEntity(playlist: PlaylistModel): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }


}