package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun add(playlist: PlaylistModel)

    suspend fun update(playlist: PlaylistModel)

    suspend fun remove(playlist: PlaylistModel)

    fun getAllPlaylists(): Flow<List<PlaylistModel>>

}