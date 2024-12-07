package com.practicum.playlistmaker.presentation.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator.provideTracksInteractor
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.player.AudioplayerActivity
import java.io.Serializable

const val TRACK_BUNDLE = "track"
const val DEF_SEARCH = "song"
const val MAX_HISTORY = 10

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_FIELD_TEXT = "SEARCH_FIELD_TEXT"
        const val SEARCH_FIELD_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val foundTracksArray = ArrayList<Track>()
    private val historyTracksArray = ArrayList<Track>()
    private val adapterFound = TrackAdapter(foundTracksArray)
    private val adapterHistory = SearchHistoryAdapter(historyTracksArray)

    //private lateinit var binding: ActivitySearchBinding
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    private var lastQuery: String = SEARCH_FIELD_DEF
    private var searchValue: String = SEARCH_FIELD_DEF
    private val handler = Handler(Looper.getMainLooper())
    private val tracksInteractor = provideTracksInteractor()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.viewTrackFoundRecycleView.layoutManager = LinearLayoutManager(this)
        binding.viewTrackFoundRecycleView.adapter = adapterFound

        binding.viewTrackHistoryRecycleView.layoutManager = LinearLayoutManager(this)
        binding.viewTrackHistoryRecycleView.adapter = adapterHistory

        binding.searchClear.isVisible = false


        binding.searchClear.setOnClickListener {
            binding.searchField.setText(SEARCH_FIELD_DEF)
            getHistory()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }


        binding.searchUpdBttn.setOnClickListener {
            showProgressBar()
            binding.searchField.setText(lastQuery)
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchClear.isVisible = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.searchField.text.isEmpty()) {
                    clearAllFound()
                    getHistory()
                } else {
                    searchDebounce()
                }
            }
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)


        binding.searchField.setOnFocusChangeListener { _, hasFocus ->
            if (
                hasFocus
                && binding.searchField.text.isEmpty()
            ) {
                getHistory()
            }

        }


        binding.searchClearHistory.setOnClickListener {
            clearHistroy()
        }


        adapterFound.onClick = { item ->
            if (clickDebounce()) {
                openAudioplayer(item)
            }
        }

        adapterHistory.onClick = { item ->
            openAudioplayer(item)
        }

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.toolbar_arrowback)
        setTitle(R.string.search_name)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarStyle)
        toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(binding.search) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_TEXT, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchField.setText(
            savedInstanceState.getString(
                SEARCH_FIELD_TEXT,
                SEARCH_FIELD_DEF
            )
        )
    }

    private fun clearHistroy() {
        tracksInteractor.clearTrackHistory()
        historyTracksArray.clear()
        adapterHistory.notifyDataSetChanged()
        binding.searchHistory.isVisible = false
    }

    private fun getHistory() {
        tracksInteractor.getTracksHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                if (!foundTracks.isNullOrEmpty()) {
                    handler.post {
                        historyTracksArray.clear()
                        historyTracksArray.addAll(foundTracks)
                        adapterHistory.notifyDataSetChanged()
                        showHistory()
                    }
                }

            }
        })
    }

    private fun showHistory() {
        if (adapterHistory.itemCount > 0 && adapterFound.itemCount == 0
        ) {
            binding.searchHistory.isVisible = true
            binding.searchNoConnect.isVisible = false
            binding.searchNotFound.isVisible = false
            binding.searchProgressBar.isVisible = false
        }
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

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun hideAll() {
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = false
        binding.searchNotFound.isVisible = false
        binding.searchProgressBar.isVisible = false
    }

    private fun showProgressBar() {
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = false
        binding.searchNotFound.isVisible = false
        binding.searchProgressBar.isVisible = true
    }

    private fun showNoConnect() {
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = true
        binding.searchNotFound.isVisible = false
        binding.searchProgressBar.isVisible = false
    }

    private fun showNotFound() {
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = false
        binding.searchNotFound.isVisible = true
        binding.searchProgressBar.isVisible = false
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}
