package com.practicum.playlistmaker.media.data.converters

import com.google.gson.Gson
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistDBConvertor {

    fun map(playlistModel: PlaylistModel): PlaylistEntity {
        return with(playlistModel) {
            PlaylistEntity(
                id = id,
                name = name,
                description = description,
                path = path,
                tracks = Gson().toJson(tracks),
                count = tracks.size
            )
        }
    }

    fun map(playlistEntity: PlaylistEntity): PlaylistModel {
        return with(playlistEntity) {
            PlaylistModel(
                id = id,
                name = name,
                description = description,
                path = path,
                tracks = fromJson(tracks).toList()
            )
        }
    }

    private fun fromJson(json: String): Array<Track> {

        return try {
            Gson().fromJson(json, Array<Track>::class.java) ?: arrayOf()
        } catch (e: NullPointerException) {
            arrayOf()
        }

    }

}