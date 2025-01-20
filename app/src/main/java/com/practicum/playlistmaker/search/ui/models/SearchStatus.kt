package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchStatus {

    data object Loading : SearchStatus
    data object Empty : SearchStatus
    data object Error : SearchStatus
    data object Clean : SearchStatus

    data class Content(
        val tracks: ArrayList<Track>
    ) : SearchStatus

    data class History(
        val historyTracks: ArrayList<Track>
    ) : SearchStatus

}