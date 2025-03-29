package com.practicum.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.media.ui.adapters.PlaylistAdapter
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

    private var adapterPlaylists = PlaylistAdapter { item ->
//        onPlaylistClick(item)
    }

    private var isClickAllowed = true

    private val clickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { allowed ->
            isClickAllowed = allowed
        }

    private fun onTrackClick(playlistModel: PlaylistModel) {
        if (clickDebounce()) {
            adapterPlaylists.notifyDataSetChanged()
//            findNavController().navigate(
//
//            )
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
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mediaButtonNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaFragment_to_newPlaylistFragment,
            )
        }

        binding.mediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.mediaRecyclerView.adapter = adapterPlaylists


        //TODO ошибка улетает в бесконечность
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