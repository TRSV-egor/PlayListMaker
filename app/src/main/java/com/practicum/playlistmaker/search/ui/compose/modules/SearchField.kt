package com.practicum.playlistmaker.search.ui.compose.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.yandexSansFamily

@Composable
fun SearchField(
    focusRequester: FocusRequester,
    viewModel: SearchViewModel,
    keyboard: SoftwareKeyboardController
) {

    val queryState by viewModel.observeQuery().observeAsState()
    var query by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(true) }

    LaunchedEffect (queryState){
        query = queryState ?: ""
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(colorResource(R.color.search_field), RoundedCornerShape(8.dp)),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                tint = colorResource(R.color.gray),
                painter = painterResource(id = R.drawable.search_search),
                contentDescription = null,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),

                )
        }
        Box(
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .focusRequester(focusRequester)
                    .clickable {

                    }
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused){
                            viewModel.getHistory()
                        }

                    },
                value = query,
                onValueChange = { query = it },
                maxLines = 1,
                textStyle = TextStyle(
                    fontFamily = yandexSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                ),
                decorationBox = { innerTextField ->
                    Row {
                        if (query.isNotEmpty()) {
                            showHint = false
                            LaunchedEffect(query) {
                                viewModel.searchDebounce(query)
                            }
                        } else {
                            showHint = true
                        }
                        innerTextField()
                    }
                },
            )
            if (showHint) {
                Text(
                    text = stringResource(R.string.search_name),
                    fontFamily = yandexSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp),

            ) {
            Icon(
                tint = colorResource(R.color.gray),
                painter = painterResource(id = R.drawable.search_clear),
                contentDescription = null,
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable(true) {
                        query = ""
                        keyboard.hide()
                        viewModel.getHistory()
                    }
            )
        }

    }

}