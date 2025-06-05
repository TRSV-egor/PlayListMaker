package com.practicum.playlistmaker.search.ui.compose.modules

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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


    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp),
    ) {
        items(trackList.size){
            TrackPosition(trackList[it], onClick)
        }
    }
}