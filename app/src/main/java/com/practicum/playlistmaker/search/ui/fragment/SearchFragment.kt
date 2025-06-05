package com.practicum.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
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
import com.practicum.playlistmaker.search.ui.compose.SearchScreen
import com.practicum.playlistmaker.search.ui.models.SearchStatus
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.NetworkBroadcastReciever
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {


    private val searchViewModel: SearchViewModel by viewModel()


    private val networkBroadcastReciever = NetworkBroadcastReciever()

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            requireContext(),
            networkBroadcastReciever,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(
                    viewModel = searchViewModel,
                    onClick = { track ->
                        searchViewModel.addTrackToHistory(track)
                        findNavController().navigate(
                            R.id.action_searchFragment_to_playerFragment,
                            bundleOf(TRACK_BUNDLE to track)
                        )
                    },
                )
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(networkBroadcastReciever)
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_BUNDLE = "track"
    }
}