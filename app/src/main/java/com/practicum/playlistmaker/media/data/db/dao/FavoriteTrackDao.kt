package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.entity.TrackEntity

@Dao
interface FavoriteTrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun remove(track: TrackEntity)

    @Query("SELECT * FROM fav_track_table WHERE trackId = :trackId")
    suspend fun contains(trackId: String): Boolean


    @Query("SELECT * FROM fav_track_table")
    suspend fun getAll(): List<TrackEntity>

}