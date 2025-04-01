package com.practicum.playlistmaker.media.domain.db.impl

import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.db.interfaces.PlaylistRepository
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun add(name: String, path: String, description: String) {
        playlistRepository.add(name, path, description)
    }

    override suspend fun update(playlist: PlaylistModel) {
        playlistRepository.update(playlist)
    }

    override suspend fun remove(playlist: PlaylistModel) {
        playlistRepository.remove(playlist)
    }

    override suspend fun addTrack(track: Track, playlist: PlaylistModel): Boolean {

        if (playlist.tracks.contains(track)) {
            return false
        }

        val mutableTracks = playlist.tracks.toMutableList()
        mutableTracks.add(track)

        playlistRepository.update(
            with(playlist) {
                PlaylistModel(
                    id = id,
                    name = name,
                    description = description,
                    path = path,
                    tracks = mutableTracks
                )
            })

        return true

    }

    override suspend fun removeTrack(track: Track, playlist: PlaylistModel): Boolean {

        if (!playlist.tracks.contains(track)) {
            return false
        }

        val mutableTracks = playlist.tracks.toMutableList()
        mutableTracks.remove(track)

        playlistRepository.update(
            with(playlist) {
                PlaylistModel(
                    id = id,
                    name = name,
                    description = description,
                    path = path,
                    tracks = mutableTracks
                )
            })

        return true

    }

    override fun getAllPlaylists(): Flow<List<PlaylistModel>> {
        return playlistRepository.getAll()
    }

    override fun receivePlaylistById(id: Long): Flow<PlaylistModel> {
        return playlistRepository.findById(id)
    }
}