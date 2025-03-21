package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {

    fun getFavoriteTrack(): Flow<List<Track>>

    suspend fun addOrRemoveFavoriteTrack(track: Track): Boolean

    suspend fun checkFavoriteTrack(track: Track): Boolean

}