package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.creator.Creator.provideTracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.SearchStatus


class SearchViewModel : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        const val SEARCH_FIELD_DEF = ""
        const val DEF_SEARCH = "song"
        const val MAX_HISTORY = 10
    }

    private val handler = Handler(Looper.getMainLooper())
    private val tracksInteractor = provideTracksInteractor()

    private val stateLiveData = MutableLiveData<SearchStatus>()
    fun observeState(): LiveData<SearchStatus> = stateLiveData

    private var searchFieldLastValue: String = SEARCH_FIELD_DEF

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchClearPressed(historyTrackCount: Int) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (historyTrackCount > 0) {
            getHistory()
        } else {
            renderState(
                SearchStatus.Clean
            )
        }
    }

    fun searchUpdButtonPressed() {
        searchDebounce(searchFieldLastValue)
    }

    fun focusOnSearchFields(hasFocus: Boolean, empty: Boolean) {
        if (
            hasFocus && empty
        ) {
            getHistory()
        }
    }

    fun clearHistory() {
        tracksInteractor.clearTrackHistory()
        renderState(
            SearchStatus.Clean
        )
    }

    fun addTrackToHistory(track: Track, historyTracksArray: ArrayList<Track>) {
        if (historyTracksArray.contains(track)) {
            historyTracksArray.remove(track)
        }

        historyTracksArray.add(0, track)

        if (historyTracksArray.size > MAX_HISTORY) {
            historyTracksArray.removeAt(MAX_HISTORY)
        }

        Runnable {
            handler.post {
                tracksInteractor.saveTracksToHistory(historyTracksArray)
            }
        }.run()
    }

    private fun getHistory() {
        tracksInteractor.getTracksHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {

                if (!foundTracks.isNullOrEmpty()) {
                    renderState(
                        SearchStatus.History(ArrayList(foundTracks))
                    )

                } else {
                    renderState(
                        SearchStatus.Clean
                    )
                }
            }
        })
    }

    fun searchDebounce(
        query: String
    ) {
        searchFieldLastValue = query
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(query) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {
            renderState(SearchStatus.Loading)

            tracksInteractor.searchTracks(
                DEF_SEARCH,
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {

                        if (foundTracks != null) {

                            when {
                                foundTracks.isEmpty() -> {
                                    renderState(
                                        SearchStatus.Empty
                                    )
                                }

                                else -> {
                                    renderState(
                                        SearchStatus.Content(
                                            tracks = ArrayList(foundTracks),
                                        )
                                    )
                                }
                            }

                        } else if (errorMessage != null) {
                            renderState(
                                SearchStatus.Error
                            )
                        }

                    }
                })
        }
    }

    private fun renderState(state: SearchStatus) {
        stateLiveData.postValue(state)
    }

}