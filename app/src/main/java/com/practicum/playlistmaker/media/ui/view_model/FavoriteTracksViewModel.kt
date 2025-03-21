package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.ui.FavoriteStatus
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel.Companion.DEF_SEARCH
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
): ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteStatus>()
    fun observeState(): LiveData<FavoriteStatus> = stateLiveData

    fun getFavorites(){

        viewModelScope.launch {
            favoriteTrackInteractor
                .getFavoriteTrack()
                .collect{ tracks ->
                    stateLiveData.value = FavoriteStatus.Favorites(tracks)
                }
        }
    }

}