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
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistListBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.media.ui.adapters.FullscreenPlaylistAdapter
import com.practicum.playlistmaker.media.ui.view_model.PlaylistListViewModel
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistListFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistListFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val PLAYLIST_BUNDLE = "playlist"
    }

    private var _binding: FragmentPlaylistListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistListViewModel by viewModel()

    private var adapterPlaylists = FullscreenPlaylistAdapter { item ->
        onPlaylistClick(item)
    }

    private var isClickAllowed = true

    private val clickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { allowed ->
            isClickAllowed = allowed
        }

    private fun onPlaylistClick(playlistModel: PlaylistModel) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistFragment,
                bundleOf(PLAYLIST_BUNDLE to playlistModel)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediaButtonNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaFragment_to_newPlaylistFragment,
            )
        }

        binding.mediaRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.mediaRecyclerView.adapter = adapterPlaylists

        viewModel.observeState().observe(viewLifecycleOwner) {
            if (it.size == 0) {
                binding.mediaRecyclerView.isVisible = false
                binding.mediaNoPlaylists.isVisible = true
            } else {
                adapterPlaylists.playlistList.clear()
                adapterPlaylists.playlistList.addAll(it)
                adapterPlaylists.notifyDataSetChanged()
                binding.mediaRecyclerView.isVisible = true
                binding.mediaNoPlaylists.isVisible = false
            }
        }

        viewModel.getPlaylists()
    }

}