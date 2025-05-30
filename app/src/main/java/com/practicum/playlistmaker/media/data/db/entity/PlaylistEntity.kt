package com.practicum.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String,
    val path: String,
    val tracks: String,
    val count: Int,
) {
    constructor(
        name: String,
        description: String,
        path: String,
        tracks: String,
        count: Int
    ) : this(
        id = 0,
        name = name,
        description = description,
        path = path,
        tracks = tracks,
        count = count
    )
}