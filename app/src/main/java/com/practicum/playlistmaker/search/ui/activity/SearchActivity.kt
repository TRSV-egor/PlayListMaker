package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.ui.SearchHistoryAdapter
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.models.SearchStatus
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel

const val TRACK_BUNDLE = "track"
const val DEF_SEARCH = "song"
const val MAX_HISTORY = 10

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_FIELD_TEXT = "SEARCH_FIELD_TEXT"
        const val SEARCH_FIELD_DEF = ""

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }



    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        val adapterFound =
            TrackAdapter((searchViewModel.foundTracksArrayLive().value) ?: arrayListOf())
        val adapterHistory =
            SearchHistoryAdapter((searchViewModel.historyTracksArrayLive().value) ?: arrayListOf())

        binding.viewTrackFoundRecycleView.layoutManager = LinearLayoutManager(this)
        binding.viewTrackFoundRecycleView.adapter = adapterFound

        binding.viewTrackHistoryRecycleView.layoutManager = LinearLayoutManager(this)
        binding.viewTrackHistoryRecycleView.adapter = adapterHistory

        binding.searchClear.isVisible = false

        searchViewModel.foundTracksArrayLive().observe(this, Observer {
            adapterFound.notifyDataSetChanged()
        })

        searchViewModel.historyTracksArrayLive().observe(this, Observer {
            adapterHistory.notifyDataSetChanged()
        })

        searchViewModel.searchFieldLive().observe(this, Observer {
            binding.searchField.setText(it)

            if (it == "") {
                showHistory()
            } else {
                searchViewModel.searchDebounce()
            }
        })


        binding.searchClear.setOnClickListener {
            //binding.searchField.setText(SEARCH_FIELD_DEF)
            //getHistory()
            searchViewModel.searchClearPressed()

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }


        binding.searchUpdBttn.setOnClickListener {
            //showProgressBar()
            //binding.searchField.setText(lastQuery)
            searchViewModel.searchUpdButtonPressed()
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //binding.searchClear.isVisible = clearButtonVisibility(s)

            }

            override fun afterTextChanged(s: Editable?) {
                searchViewModel.searchDebounce(
                   query =  s?.toString() ?: ""
                )
//                if (binding.searchField.text.isEmpty()) {
//                    clearAllFound()
//                    getHistory()
//                } else {
//                    searchDebounce()
//                }
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

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
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

    private fun showHistory() {
//        if (adapterHistory.itemCount > 0 && adapterFound.itemCount == 0
//        ) {
        binding.searchHistory.isVisible = true
        binding.searchNoConnect.isVisible = false
        binding.searchNotFound.isVisible = false
        binding.searchProgressBar.isVisible = false
//        }
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

    private fun render(state: SearchStatus) {
        when (state) {
            is SearchStatus.Content -> showContent(state.movies)
            is SearchStatus.Empty -> showEmpty(state.message)
            is SearchStatus.Error -> showError(state.errorMessage)
            is SearchStatus.Loading -> showProgressBar()
        }
    }

}
