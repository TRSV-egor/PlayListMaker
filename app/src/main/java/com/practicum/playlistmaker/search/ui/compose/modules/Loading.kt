package com.practicum.playlistmaker.search.ui.compose.modules

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R

@Composable
fun Loading() {
    CircularProgressIndicator(
        modifier = Modifier
            .width(44.dp)
            .padding(top = 192.dp),
        color = colorResource(id = R.color.theme_white_black),
        trackColor = colorResource(id = R.color.blue),
    )
}