package com.practicum.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
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
import com.practicum.playlistmaker.media.ui.compose.PlaylistListScreen
import com.practicum.playlistmaker.media.ui.view_model.PlaylistListViewModel
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistListFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistListFragment()
        const val PLAYLIST_BUNDLE = "playlist"
    }

    private val viewModel: PlaylistListViewModel by viewModel()

    override fun onResume() {
        super.onResume()
        if (viewModel.observeState().isInitialized) {
            viewModel.getPlaylists()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistListScreen(
                    onPlaylistClick = { playlistModel ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_playlistFragment,
                            bundleOf(PLAYLIST_BUNDLE to playlistModel)
                        )
                    },
                    onButtonClick = {
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_newPlaylistFragment,
                        )
                    }

                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaylists()
    }

}