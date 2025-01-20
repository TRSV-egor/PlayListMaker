package com.practicum.playlistmaker.search.ui.activity

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
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchHistoryAdapter
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.models.SearchStatus
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable


class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_BUNDLE = "track"
    }


    private val handler = Handler(Looper.getMainLooper())

    private var adapterFound = TrackAdapter { item ->
        onTrackClick(item)
    }


    private var adapterHistory = SearchHistoryAdapter { item ->
        onTrackClick(item)
    }


    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModel()

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

        searchViewModel.observeState().observe(this) {
            render(it)
        }

        binding.searchClear.setOnClickListener {
            searchViewModel.searchClearPressed(
                historyTrackCount = adapterHistory.itemCount
            )
            binding.searchField.setText("")


            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }

        binding.searchUpdBttn.setOnClickListener {
            searchViewModel.searchUpdButtonPressed()
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchClear.isVisible = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    searchViewModel.searchClearPressed(adapterHistory.itemCount)
                } else {
                    searchViewModel.searchDebounce(
                        query = s.toString()
                    )
                }

            }
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)


        binding.searchField.setOnFocusChangeListener { _, hasFocus ->
            searchViewModel.focusOnSearchFields(hasFocus, binding.searchField.text.isEmpty())
        }


        binding.searchClearHistory.setOnClickListener {
            searchViewModel.clearHistory()
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

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun showHistory(tracks: ArrayList<Track>) {
        adapterHistory.historyTracks.clear()
        adapterHistory.historyTracks.addAll(tracks)
        adapterHistory.notifyDataSetChanged()
        binding.viewTrackFoundRecycleView.isVisible = false
        binding.searchHistory.isVisible = true
        binding.searchNoConnect.isVisible = false
        binding.searchProgressBar.isVisible = false
        binding.searchNotFound.isVisible = false

    }

    private fun hideAll() {
        binding.viewTrackFoundRecycleView.isVisible = false
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = false
        binding.searchNotFound.isVisible = false
        binding.searchProgressBar.isVisible = false
    }

    private fun showProgressBar() {
        binding.viewTrackFoundRecycleView.isVisible = false
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = false
        binding.searchNotFound.isVisible = false
        binding.searchProgressBar.isVisible = true
    }

    private fun showNoConnect() {
        binding.viewTrackFoundRecycleView.isVisible = false
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = true
        binding.searchNotFound.isVisible = false
        binding.searchProgressBar.isVisible = false
    }

    private fun showNotFound() {
        binding.viewTrackFoundRecycleView.isVisible = false
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = false
        binding.searchProgressBar.isVisible = false
        binding.searchNotFound.isVisible = true
    }

    private fun showContent(tracks: ArrayList<Track>) {
        adapterFound.foundTracks.clear()
        adapterFound.foundTracks.addAll(tracks)
        adapterFound.notifyDataSetChanged()
        binding.searchHistory.isVisible = false
        binding.searchNoConnect.isVisible = false
        binding.searchNotFound.isVisible = false
        binding.searchProgressBar.isVisible = false
        binding.viewTrackFoundRecycleView.isVisible = true
    }

    private fun render(state: SearchStatus) {
        when (state) {
            is SearchStatus.Content -> showContent(state.tracks)
            is SearchStatus.Empty -> showNotFound()
            is SearchStatus.Error -> showNoConnect()
            is SearchStatus.Loading -> showProgressBar()
            is SearchStatus.History -> showHistory(state.historyTracks)
            is SearchStatus.Clean -> hideAll()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun onTrackClick(track: Track) {

        if (clickDebounce()) {
            searchViewModel.addTrackToHistory(track, adapterHistory.historyTracks)
            adapterHistory.notifyDataSetChanged()
            val intent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
            intent.putExtra(TRACK_BUNDLE, track as Serializable)
            startActivity(intent)
        }
    }

}
