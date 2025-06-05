package com.practicum.playlistmaker.search.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.compose.modules.History
import com.practicum.playlistmaker.search.ui.compose.modules.Loading
import com.practicum.playlistmaker.search.ui.compose.modules.NoConnection
import com.practicum.playlistmaker.search.ui.compose.modules.NotFound
import com.practicum.playlistmaker.search.ui.compose.modules.SearchField
import com.practicum.playlistmaker.search.ui.compose.modules.SearchResult
import com.practicum.playlistmaker.search.ui.models.SearchStatus
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import kotlinx.coroutines.delay


@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onClick: (Track) -> Unit
) {

    //scroll
    val scrollState = rememberScrollState()

    //states
    val screenState by viewModel.observeState().observeAsState()


    //show keyboard
    val showKeyboard = remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(focusRequester) {
        if (showKeyboard.value) {
            focusRequester.requestFocus()
            delay(100)
            keyboard?.show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchField(focusRequester, viewModel, keyboard)

        when (screenState) {
            SearchStatus.Clean -> {

            }

            SearchStatus.Error -> NoConnection(viewModel = viewModel)
            SearchStatus.Loading -> Loading()
            is SearchStatus.Content -> {
                SearchResult(
                    scrollState = scrollState,
                    trackList = (screenState as SearchStatus.Content).tracks,
                    onClick = onClick,
                )
            }

            SearchStatus.Empty -> NotFound()
            is SearchStatus.History -> {
                History(
                    scrollState = scrollState,
                    trackList = (screenState as SearchStatus.History).historyTracks,
                    onClickHistory = onClick,
                    viewModel = viewModel
                )
            }

            null -> {}
        }
    }
}