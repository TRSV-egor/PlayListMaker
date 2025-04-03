package com.practicum.playlistmaker.media.domain.db.interfaces

import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun add(name: String, path: String, description: String)

    suspend fun update(playlist: PlaylistModel)

    suspend fun remove(playlist: PlaylistModel)

    suspend fun globalTrackSearch(track: Track): List<Long>

    fun findById(id: Long): Flow<PlaylistModel>

    fun getAll(): Flow<List<PlaylistModel>>

}