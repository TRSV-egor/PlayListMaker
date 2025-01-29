package com.practicum.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.databinding.FragmentSearchBinding

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchHistoryAdapter
import com.practicum.playlistmaker.search.ui.TrackAdapter

import com.practicum.playlistmaker.search.ui.models.SearchStatus
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())

    private var adapterFound = TrackAdapter { item ->
        onTrackClick(item)
    }


    private var adapterHistory = SearchHistoryAdapter { item ->
        onTrackClick(item)
    }

    private val searchViewModel: SearchViewModel by viewModel()

    private var isClickAllowed = true

    private lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                bundleOf(TRACK_BUNDLE to track)
            )
//            val intent = Intent(this@SearchActivity, AudioPlayerActivity::class.java)
//            intent.putExtra(TRACK_BUNDLE, track as Serializable)
//            startActivity(intent)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_BUNDLE = "track"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            SearchFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }
}