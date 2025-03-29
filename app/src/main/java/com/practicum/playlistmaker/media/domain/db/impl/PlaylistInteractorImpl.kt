package com.practicum.playlistmaker.media.domain.db.impl

import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun add(playlist: PlaylistModel) {
        playlistRepository.add(playlist)
    }

    override suspend fun remove(playlist: PlaylistModel) {
        playlistRepository.remove(playlist)
    }

    override suspend fun update(playlist: PlaylistModel) {
        playlistRepository.update(playlist)
    }

    override fun getAllPlaylists(): Flow<List<PlaylistModel>> {
        return playlistRepository.getAll()
    }

}