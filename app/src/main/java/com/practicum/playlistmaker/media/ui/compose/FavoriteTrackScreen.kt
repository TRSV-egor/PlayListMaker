package com.practicum.playlistmaker.media.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.FavoriteStatus
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.compose.modules.TrackPosition
import com.practicum.playlistmaker.util.yandexSansFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteTrackScreen(
    //viewModel: FavoriteTracksViewModel,
    onClick:(Track)-> Unit) {

    val viewModel: FavoriteTracksViewModel = koinViewModel()
   val trackListState by viewModel.observeState().observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {


        when (trackListState) {
            is FavoriteStatus.Loading -> Empty()
            is FavoriteStatus.Empty -> Empty()
            is FavoriteStatus.Favorites -> TrackList((trackListState as FavoriteStatus.Favorites).tracks, onClick)

        }
    }
}

@Composable
private fun Empty() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 106.dp)
    ) {
        Image(
            modifier = Modifier.padding(top = 33.dp),
            painter = painterResource(id = R.drawable.search_404),
            contentDescription = null,
            alignment = Alignment.TopCenter
        )
        Text(
            text = stringResource(id = R.string.media_fragment_favorites_empty),
            textAlign = TextAlign.Center,
            fontFamily = yandexSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun TrackList(trackList: List<Track>, onClick:(Track)-> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 20.dp)
    ) {

        for (track in trackList){
            TrackPosition(track, onClick)
        }

    }
}

@Preview
@Composable
private fun MyPreview(){
    TrackPosition(
        Track(
           trackId= "",
       trackName= "SuperDuper Song Ever made EGTgkjnsdfghndfognsoaldj",
   artistName= "ABOABABOABABOABABOABABOABABOABABOABABOABABOABABOAB",
   trackTime= "08:30",
   artworkUrl100= "",
   releaseDate= "",
   primaryGenreName= "",
   country= "",
   collectionName= "",
   previewUrl= "",
        ), {})
}
