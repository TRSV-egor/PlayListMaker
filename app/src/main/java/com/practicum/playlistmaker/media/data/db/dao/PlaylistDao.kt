package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(playlist: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun remove(playlist: PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getAll(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    suspend fun findById(id: Long): PlaylistEntity

}