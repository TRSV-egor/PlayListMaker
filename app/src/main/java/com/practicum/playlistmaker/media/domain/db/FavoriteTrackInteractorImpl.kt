package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : FavoriteTrackInteractor {

    override fun getFavoriteTrack(): Flow<List<Track>> {
        return favoriteTrackRepository.getAll()
    }

    override suspend fun addOrRemoveFavoriteTrack(track: Track): Boolean {
        if (favoriteTrackRepository.contains(track)) {
            favoriteTrackRepository.add(track)
            return true
        } else {
            favoriteTrackRepository.remove(track)
            return false
        }
    }

    override suspend fun checkFavoriteTrack(track: Track): Boolean {
        return favoriteTrackRepository.contains(track)
    }

}