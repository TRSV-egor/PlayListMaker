package com.practicum.playlistmaker.media.ui.compose

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistListViewModel
import com.practicum.playlistmaker.util.yandexSansFamily
import androidx.core.net.toUri
import com.practicum.playlistmaker.util.Declination
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistListScreen(
    onPlaylistClick: (PlaylistModel) -> Unit,
    onButtonClick: () -> Unit,
) {

    val viewModel: PlaylistListViewModel = koinViewModel()
    val scrollState = rememberScrollState()
    val playlistState by viewModel.observeState().observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {onButtonClick()},
            enabled = true,
            shape = RoundedCornerShape(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.toolbar))
        ) {
            Text(
                text = "Новый плейлист",
                fontFamily = yandexSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colorResource(id = R.color.theme_white_black)
                )
        }

        playlistState?.let {
            if (playlistState.isNullOrEmpty()) {
                Empty()
            } else {
                ListPlaylist(
                    scrollState = scrollState,
                    list = it,
                    onPlaylistClick = onPlaylistClick
                )
            }
        }


    }
}

@Composable
private fun Empty() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(top = 33.dp),
            painter = painterResource(id = R.drawable.search_404),
            contentDescription = null,
            alignment = Alignment.TopCenter
        )
        Text(
            text = stringResource(id = R.string.media_fragment_playlist_empty),
            textAlign = TextAlign.Center,
            fontFamily = yandexSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            color = colorResource(id = R.color.toolbar)
        )
    }
}

@Composable
private fun ListPlaylist(
    scrollState: ScrollState,
    list: List<PlaylistModel>,
    onPlaylistClick: (PlaylistModel) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        columns = GridCells.Fixed(2),
    ) {
        items(list) { playlist ->
            Playlist(
                playlist = playlist,
                onPlaylistClick = onPlaylistClick
            )
        }
    }
}

@Composable
private fun Playlist(
    playlist: PlaylistModel,
    onPlaylistClick: (PlaylistModel) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(bottom = 16.dp)
        .clickable(true) { onPlaylistClick(playlist) }
    ) {
        Column {

            if (playlist.path != ""){
                AsyncImage(
                    model = playlist.path.toUri(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.playlist_view_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }


            Text(
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 4.dp),
                text = playlist.name,
                fontFamily = yandexSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = colorResource(id = R.color.toolbar)
            )
            Text(
                text = "${playlist.tracks.size} ${Declination.getTracks(playlist.tracks.size)}",
                fontFamily = yandexSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = colorResource(id = R.color.toolbar)
            )
        }
    }
}
