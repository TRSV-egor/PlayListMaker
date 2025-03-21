package com.practicum.playlistmaker.media.ui

import com.practicum.playlistmaker.search.domain.models.Track

interface FavoriteStatus {

    data object Loading : FavoriteStatus
    data object Empty : FavoriteStatus
    data class Favorites(
        val tracks: List<Track>
    ) : FavoriteStatus

}