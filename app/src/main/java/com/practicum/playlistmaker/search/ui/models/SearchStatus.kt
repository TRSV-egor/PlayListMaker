package com.practicum.playlistmaker.search.ui.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchStatus {

    object Loading : SearchStatus

    data class Content(
        val tracks: List<Track>
    ) : SearchStatus

    data class Error(
        val errorMessage: String
    ) : SearchStatus

    data class Empty(
        val message: String
    ) : SearchStatus

}