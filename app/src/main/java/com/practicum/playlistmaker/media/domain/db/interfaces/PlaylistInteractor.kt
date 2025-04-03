package com.practicum.playlistmaker.media.domain.db.interfaces

import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun add(name: String, path: String, description: String)

    suspend fun addTrack(track: Track, playlist: PlaylistModel): Boolean

    suspend fun removeTrack(track: Track, playlist: PlaylistModel): Boolean

    suspend fun remove(playlist: PlaylistModel)

    suspend fun update(playlist: PlaylistModel)

    fun receivePlaylistById(id: Long): Flow<PlaylistModel>

    fun getAllPlaylists(): Flow<List<PlaylistModel>>

}