package com.practicum.playlistmaker.media.domain.db.impl

import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistInteractor: PlaylistInteractor
) : PlaylistInteractor {

    override suspend fun add(playlist: PlaylistModel) {
        playlistInteractor.add(playlist)
    }

    override suspend fun remove(playlist: PlaylistModel) {
        playlistInteractor.remove(playlist)
    }

    override suspend fun update(playlist: PlaylistModel) {
        playlistInteractor.update(playlist)
    }

    override fun getAllPlaylists(): Flow<List<PlaylistModel>> {
        return playlistInteractor.getAllPlaylists()
    }

}