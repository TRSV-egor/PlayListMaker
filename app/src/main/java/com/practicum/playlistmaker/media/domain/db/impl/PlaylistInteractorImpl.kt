package com.practicum.playlistmaker.media.domain.db.impl

import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
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

    override suspend fun update(track: Track, playlist: PlaylistModel) {

        val mutableTracks = playlist.tracks.toMutableList()
        mutableTracks.add(track)

        playlistRepository.update(
            with(playlist) {
                PlaylistModel(
                    name = name,
                    description = description,
                    path = path,
                    tracks = mutableTracks
                )
            })

    }

    override fun getAllPlaylists(): Flow<List<PlaylistModel>> {
        return playlistRepository.getAll()
    }

}