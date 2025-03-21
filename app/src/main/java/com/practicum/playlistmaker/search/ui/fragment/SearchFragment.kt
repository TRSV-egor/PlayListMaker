package com.practicum.playlistmaker.search.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchHistoryAdapter
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.models.SearchStatus
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {


    private var adapterFound = TrackAdapter { item ->
        onTrackClick(item)
    }

    private var adapterHistory = SearchHistoryAdapter { item ->
        onTrackClick(item)
    }

    private val searchViewModel: SearchViewModel by viewModel()

    private var isClickAllowed = true

    private lateinit var binding: FragmentSearchBinding

    private val clickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { allowed ->
            isClickAllowed = allowed
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewTrackFoundRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.viewTrackFoundRecycleView.adapter = adapterFound

        binding.viewTrackHistoryRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.viewTrackHistoryRecycleView.adapter = adapterHistory

        binding.searchClear.isVisible = false

        searchViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.searchClear.setOnClickListener {
            searchViewModel.searchClearPressed(
                historyTrackCount = adapterHistory.itemCount
            )
            binding.searchField.setText("")


            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }

        binding.searchUpdBttn.setOnClickListener {
            searchViewModel.searchUpdButtonPressed()
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchClear.isVisible = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {

                if (s.isNullOrEmpty()) {
                    searchViewModel.searchClearPressed(adapterHistory.itemCount)
                }

                searchViewModel.searchDebounce(
                    query = s.toString()
                )

            }
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)


        binding.searchField.setOnFocusChangeListener { _, hasFocus ->
            searchViewModel.focusOnSearchFields(hasFocus, binding.searchField.text.isEmpty())
        }


        binding.searchClearHistory.setOnClickListener {
            searchViewModel.clearHistory()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun showHistory(tracks: List<Track>) {
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

    private fun showContent(tracks: List<Track>) {
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
            clickDebounce(true)
        }
        return current
    }

    private fun onTrackClick(track: Track) {

        if (clickDebounce()) {
            searchViewModel.addTrackToHistory(track, adapterHistory.historyTracks)
            adapterHistory.notifyDataSetChanged()
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                bundleOf(TRACK_BUNDLE to track)
            )
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_BUNDLE = "track"
    }
}