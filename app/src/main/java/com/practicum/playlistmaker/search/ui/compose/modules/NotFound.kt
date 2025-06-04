package com.practicum.playlistmaker.search.ui.compose.modules

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.yandexSansFamily

@Composable
fun NotFound() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 102.dp)
    ) {
        Image(
            modifier = Modifier.padding(top = 33.dp),
            painter = painterResource(id = R.drawable.search_404),
            contentDescription = null,
            alignment = Alignment.TopCenter
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.search_404),
            textAlign = TextAlign.Center,
            fontFamily = yandexSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            color = colorResource(id = R.color.toolbar)
        )
    }
}