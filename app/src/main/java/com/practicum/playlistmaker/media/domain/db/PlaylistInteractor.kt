package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun add(name: String, path: String, description: String)

    suspend fun update(track: Track, playlist: PlaylistModel): Boolean

    suspend fun remove(playlist: PlaylistModel)

    fun getAllPlaylists(): Flow<List<PlaylistModel>>

}