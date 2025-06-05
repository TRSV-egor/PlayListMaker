package com.practicum.playlistmaker.search.ui.compose.modules

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.yandexSansFamily

@Composable
fun History(
    scrollState: ScrollState,
    trackList: List<Track>,
    onClickHistory: (Track) -> Unit,
    viewModel: SearchViewModel
) {

    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp),
        //.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(1) {
            Text(
                text = stringResource(id = R.string.search_history),
                fontFamily = yandexSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 19.sp,
                modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
                color = colorResource(id = R.color.toolbar)
            )
        }


        items(trackList.size) {
            TrackPosition(trackList[it], onClickHistory)
        }

        items(1) {
            Button(
                modifier = Modifier.padding(top = 24.dp),
                onClick = {
                    viewModel.clearHistory()
                },
                enabled = true,
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.toolbar))
            ) {
                Text(
                    text = stringResource(id = R.string.search_clear_history),
                    fontFamily = yandexSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.theme_white_black)

                )
            }
        }


    }
}