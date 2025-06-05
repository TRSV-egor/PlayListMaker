package com.practicum.playlistmaker.search.ui.compose.modules

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.yandexSansFamily

@Composable
fun TrackPosition(track: Track, onClick:(Track)-> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(start = 13.dp)
            .padding(end = 12.dp)
            .fillMaxWidth()
            .clickable(enabled = true) { onClick(track) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = track.artworkUrl100,
            placeholder = painterResource(id = R.drawable.playlist_view_placeholder),
            contentDescription = null,
            modifier = Modifier.size(45.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(horizontal = 8.dp),
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = yandexSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = colorResource(id = R.color.toolbar)
            )
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.wrapContentWidth(),
                    fontFamily = yandexSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = colorResource(id = R.color.toolbar)

                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(5.dp)
                        .wrapContentWidth(),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.track_dot),
                        contentDescription = null,
                    )
                }
                Text(
                    text = track.trackTime,
                    maxLines = 1,
                    fontFamily = yandexSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = colorResource(id = R.color.toolbar)
                )
            }

        }

        Image(
            painter = painterResource(id = R.drawable.settings_track_arrow_forward),
            contentDescription = null,
            alignment = Alignment.CenterEnd,
        )
    }
}

@Preview(device = "id:pixel_5", showSystemUi = false)
@Composable
private fun MyPreview(){
    TrackPosition(
        Track(
            trackId= "",
            trackName= "SuperDuper ",
            artistName= "ABOABAB",
            trackTime= "08:30",
            artworkUrl100= "",
            releaseDate= "",
            primaryGenreName= "",
            country= "",
            collectionName= "",
            previewUrl= "",
        ), {})
}