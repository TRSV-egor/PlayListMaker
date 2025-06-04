package com.practicum.playlistmaker.search.ui.compose.modules

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.search.domain.models.Track

@Composable
fun SearchResult(
    scrollState: ScrollState,
    trackList: List<Track>,
    onClick: (Track) -> Unit,
) {


    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .verticalScroll(scrollState),
    ) {
        for (track in trackList) {
            TrackPosition(track, onClick)
        }
    }
}