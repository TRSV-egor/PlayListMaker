package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {

    fun getAll(): Flow<List<Track>>

    suspend fun add(track: Track)

    suspend fun remove(track: Track)

    suspend fun contains(track: Track): Boolean

}