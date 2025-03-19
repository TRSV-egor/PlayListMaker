package com.practicum.playlistmaker.media.data.converters

import com.practicum.playlistmaker.media.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbConvertor {

    fun map(track: TrackDto): TrackEntity {
        return with(track) {
            TrackEntity(
                trackId,
                trackName,
                artistName ?: "",
                trackTime,
                artworkUrl100 ?: "",
                releaseDate ?: "",
                primaryGenreName ?: "",
                country ?: "",
                collectionName ?: "",
                previewUrl ?: "",
            )
        }

    }

    fun map(track: TrackEntity): Track {
        return with(track) {
            Track(
                trackId,
                trackName,
                artistName,
                trackTime,
                artworkUrl100,
                releaseDate,
                primaryGenreName,
                country,
                collectionName,
                previewUrl,
            )
        }

    }


    fun map(track: Track): TrackEntity {
        return with(track) {
            TrackEntity(
                trackId,
                trackName,
                artistName,
                trackTime,
                artworkUrl100,
                releaseDate,
                primaryGenreName,
                country,
                collectionName,
                previewUrl,
            )
        }
    }