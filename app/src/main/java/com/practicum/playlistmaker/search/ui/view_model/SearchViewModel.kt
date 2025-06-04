package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchStatus
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.launch


class SearchViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val SEARCH_FIELD_DEF = ""
        const val DEF_SEARCH = "song"
        const val MAX_HISTORY = 10
    }

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    private val stateLiveData = MutableLiveData<SearchStatus>()
    fun observeState(): LiveData<SearchStatus> = stateLiveData

    private val searchFieldLastValue = MutableLiveData<String>()
    fun observeQuery(): LiveData<String> = searchFieldLastValue

    override fun onCleared() {
        trackSearchDebounce(SEARCH_FIELD_DEF)
    }

    fun searchUpdButtonPressed() {
        trackSearchDebounce(searchFieldLastValue.value ?: SEARCH_FIELD_DEF)
    }

    fun clearHistory() {
        tracksInteractor.clearTrackHistory()
        renderState(
            SearchStatus.Clean
        )
    }

    fun addTrackToHistory(
        track: Track,
    ) {

        var historyTracksArray: MutableList<Track> = mutableListOf()


        viewModelScope.launch {
            tracksInteractor
                .getTracksHistory()
                .collect { pair ->
                    historyTracksArray = processHistoryResult(pair.first, pair.second, false)
                }
        }


        if (historyTracksArray.contains(track)) {
            historyTracksArray.remove(track)
        }

        historyTracksArray.add(0, track)

        if (historyTracksArray.size > MAX_HISTORY) {
            historyTracksArray.removeAt(MAX_HISTORY)
        }

        viewModelScope.launch {
            tracksInteractor.saveTracksToHistory(historyTracksArray)
        }

    }

    fun getHistory() {
        onCleared()
        viewModelScope.launch {
            tracksInteractor
                .getTracksHistory()
                .collect { pair ->
                    processHistoryResult(pair.first, pair.second, true)
                }
        }
    }

    private fun processHistoryResult(
        foundTracks: List<Track>?,
        errorMessage: String?,
        generateState: Boolean
    ): MutableList<Track> {
        val tracks = mutableListOf<Track>()

        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        if (tracks.isNotEmpty() && generateState) {
            renderState(
                SearchStatus.History(foundTracks ?: listOf())
            )

        } else if (generateState) {
            renderState(
                SearchStatus.Clean
            )
        }

        return tracks
    }

    fun searchDebounce(
        query: String
    ) {
        if (query != searchFieldLastValue.value) {
            searchFieldLastValue.value = query
            trackSearchDebounce(query)
        }

    }

    private fun searchRequest(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {
            renderState(SearchStatus.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(DEF_SEARCH, newSearchText)
                    .collect { pair ->
                        processSearchResult(pair.first, pair.second)
                    }
            }
        }

    }

    private fun processSearchResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()

        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            tracks.isEmpty() && errorMessage.isNullOrEmpty() -> {
                renderState(
                    SearchStatus.Empty
                )
            }

            !errorMessage.isNullOrEmpty() -> {
                renderState(
                    SearchStatus.Error
                )
            }

            else -> {
                renderState(
                    SearchStatus.Content(
                        tracks = tracks
                    )
                )
            }
        }

    }

    private fun renderState(state: SearchStatus) {
        stateLiveData.postValue(state)
    }


}