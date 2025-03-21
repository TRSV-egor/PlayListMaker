package com.practicum.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.media.ui.FavoriteAdapter
import com.practicum.playlistmaker.media.ui.FavoriteStatus
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.fragment.SearchFragment.Companion.TRACK_BUNDLE
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteTracksViewModel by viewModel()

    private var adapterFavorites = FavoriteAdapter { item ->
        onTrackClick(item)
    }

    private val clickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { allowed ->
            isClickAllowed = allowed
        }

    private fun onTrackClick(track: Track) {

        if (clickDebounce()) {
            adapterFavorites.notifyDataSetChanged()
            findNavController().navigate(
                R.id.action_mediaFragment_to_playerFragment,
                bundleOf(TRACK_BUNDLE to track)
            )
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

    private var isClickAllowed = true


    companion object {
        fun newInstance() = FavoriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        favoriteViewModel.getFavorites()
    }

    private fun render(state: FavoriteStatus) {
        when (state) {
            is FavoriteStatus.Loading -> showProgressBar()
            is FavoriteStatus.Empty -> showEmpty()
            is FavoriteStatus.Favorites -> showContent(ArrayList(state.tracks))
        }
    }

    private fun showContent(tracks: ArrayList<Track>) {
        adapterFavorites.favoriteTracks.clear()
        adapterFavorites.favoriteTracks.addAll(tracks)
        adapterFavorites.notifyDataSetChanged()
        binding.favoriteProgressBar.isVisible = false
        binding.fragmentFavoriteContent.isVisible = true
        binding.fragmentFavoriteEmpty.isVisible = false
    }

    private fun showProgressBar() {
        binding.favoriteProgressBar.isVisible = true
        binding.fragmentFavoriteContent.isVisible = false
        binding.fragmentFavoriteEmpty.isVisible = false
    }

    private fun showEmpty() {
        binding.favoriteProgressBar.isVisible = false
        binding.fragmentFavoriteContent.isVisible = false
        binding.fragmentFavoriteEmpty.isVisible = true
    }
}