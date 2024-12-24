package com.practicum.playlistmaker.search.ui.view_model

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.creator.Creator.provideTracksInteractor
import com.practicum.playlistmaker.player.ui.activity.AudioplayerActivity
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.activity.DEF_SEARCH
import com.practicum.playlistmaker.search.ui.activity.MAX_HISTORY
import com.practicum.playlistmaker.search.ui.activity.SearchActivity.Companion.CLICK_DEBOUNCE_DELAY
import com.practicum.playlistmaker.search.ui.activity.SearchActivity.Companion.SEARCH_FIELD_DEF
import com.practicum.playlistmaker.search.ui.activity.TRACK_BUNDLE
import java.io.Serializable

class SearchViewModel : ViewModel() {
    //private val foundTracksArray = ArrayList<Track>()
    //private val historyTracksArray = ArrayList<Track>()

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        //private var lastQuery: String = SEARCH_FIELD_DEF
        //private var searchValue: String = SEARCH_FIELD_DEF
    }


    private val handler = Handler(Looper.getMainLooper())
    private val tracksInteractor = provideTracksInteractor()

    private var foundTracksArrayLiveMutable = MutableLiveData<ArrayList<Track>>()
    var foundTracksArrayLive: LiveData<ArrayList<Track>> = foundTracksArrayLiveMutable

    private var historyTracksArrayLiveMutable = MutableLiveData<ArrayList<Track>>()
    var historyTracksArrayLive: LiveData<ArrayList<Track>> = foundTracksArrayLiveMutable

    private var searchFieldLiveMutable = MutableLiveData<String>()
    var searchFieldLive: LiveData<String> = searchFieldLiveMutable

    fun searchClearPressed() {
        searchFieldLiveMutable.value = SEARCH_FIELD_DEF
        getHistory()
    }

    private val searchRunnable = Runnable {

        if (binding.searchField.text.isNotEmpty()) {
            lastQuery = binding.searchField.text.toString()
            clearAllFound()
            showProgressBar()

            tracksInteractor.searchTracks(
                DEF_SEARCH,
                binding.searchField.text.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?) {
                        handler.post {
                            if (foundTracks == null) {
                                showNoConnect()
                            } else {
                                foundTracksArray.addAll(foundTracks)
                                hideAll()
                                adapterFound.notifyDataSetChanged()
                                if (adapterFound.itemCount == 0) {
                                    showNotFound()
                                }
                            }

                        }
                    }
                })


        }

    }

    private var isClickAllowed = true

    private fun clearHistroy() {
        tracksInteractor.clearTrackHistory()
        historyTracksArray.clear()
        adapterHistory.notifyDataSetChanged()
        binding.searchHistory.isVisible = false
    }

    fun getHistory() {
        tracksInteractor.getTracksHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                if (!foundTracks.isNullOrEmpty()) {
                    handler.post {
//                        historyTracksArray.clear()
//                        historyTracksArray.addAll(foundTracks)
//                        adapterHistory.notifyDataSetChanged()
//                        showHistory()
                        historyTracksArrayLiveMutable.value?.clear()
                        historyTracksArrayLiveMutable.value?.addAll(foundTracks)


                    }
                }

            }
        })
    }



    private fun addTrackToHistory(track: Track) {
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
                adapterHistory.notifyDataSetChanged()
            }
        }.run()
    }


    fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }



    private fun clearAllFound() {
        foundTracksArray.clear()
        adapterFound.notifyDataSetChanged()
    }

    private fun openAudioplayer(
        item: Track,
    ) {
        addTrackToHistory(item)
        val intent = Intent(this, AudioplayerActivity::class.java)
        intent.putExtra(TRACK_BUNDLE, item as Serializable)
        startActivity(intent)
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}