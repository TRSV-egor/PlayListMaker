package com.practicum.playlistmaker.media.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistViewerBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.media.ui.fragments.PlaylistListFragment.Companion.PLAYLIST_BUNDLE
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.fragment.SearchFragment.Companion.TRACK_BUNDLE
import com.practicum.playlistmaker.util.DateFormater
import com.practicum.playlistmaker.util.Declination
import com.practicum.playlistmaker.util.Declination.getMinutes
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var tracksInPlaylistBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var mainMenuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val viewModel: PlaylistViewModel by viewModel()

    private var isClickAllowed = true

    private var _binding: FragmentPlaylistViewerBinding? = null
    private val binding get() = _binding!!

    private var adapterPlaylistContent = TrackAdapter { item, longClick ->
        if (longClick) onLongTrackClick(item) else onTrackClick(item)
    }

    private val clickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { allowed ->
            isClickAllowed = allowed
        }

    override fun onResume() {
        super.onResume()
        viewModel.updateFromDataBase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                viewModel.fillPlaylistViewer(
                    it.getParcelable(
                        PLAYLIST_BUNDLE,
                        PlaylistModel::class.java
                    )
                )
            } else {
                viewModel.fillPlaylistViewer(it.getParcelable(PLAYLIST_BUNDLE))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistRecyclerview.adapter = adapterPlaylistContent

        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        tracksInPlaylistBottomSheetBehavior =
            BottomSheetBehavior.from(binding.tracksInPlaylistBottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }


        mainMenuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        tracksInPlaylistBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        BottomSheetBehavior.STATE_COLLAPSED
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
            }
        })

        mainMenuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = 0.5f
            }
        })

        viewModel.observerPlaylist().observe(viewLifecycleOwner) {
            if (it.path != "") {
                binding.placeholder.setImageURI(it.path.toUri())
            }
            binding.name.text = it.name
            binding.description.text = it.description
            val summaryTime = summaryTimeOfPlaylist(it)
            binding.summaryTime.text = "$summaryTime ${getMinutes(summaryTime)}"
            binding.trackCount.text = "${it.tracks.size} ${Declination.getTracks(it.tracks.size)}"

            adapterPlaylistContent.foundTracks.clear()
            adapterPlaylistContent.foundTracks.addAll(it.tracks.reversed())
            adapterPlaylistContent.notifyDataSetChanged()
            binding.emptyPlaylist.isVisible = adapterPlaylistContent.foundTracks.size == 0


            binding.menuName.text = it.name
            binding.menuCount.text = "${it.tracks.size} ${Declination.getTracks(it.tracks.size)}"
            binding.menuPlaceholder.setImageURI(it.path.toUri())
        }

        binding.share.setOnClickListener {
            share()
        }

        binding.menu.setOnClickListener {
            mainMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.menuShare.setOnClickListener {
            share()
            mainMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.menuDelete.setOnClickListener {
            mainMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            confirmDelete()
        }

        binding.menuEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_newPlaylistFragment,
                bundleOf(PLAYLIST_BUNDLE to viewModel.observerPlaylist().value)
            )
        }


    }

    private fun summaryTimeOfPlaylist(playlistModel: PlaylistModel): Int {
        var result = 0L

        playlistModel.tracks.forEach {
            result += DateFormater.toLong(it.trackTime)
        }

        return ((result / 1000) / 60).toInt()
    }


    private fun onLongTrackClick(track: Track) {

        if (clickDebounce()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.fragment_playlist_new_dialog_title))
                .setMessage(getString(R.string.fragment_playlist_new_dialog_message))
                .setNegativeButton(getString(R.string.fragment_playlist_new_dialog_nevative)) { _, _ -> }
                .setPositiveButton(getString(R.string.fragment_playlist_new_dialog_positive)) { _, _ ->
                    viewModel.removeTrackFromPlaylist(track)
                }
                .show()
        }
    }

    private fun onTrackClick(track: Track) {

        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
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

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${getString(R.string.fragment_playlist_new_dialog_title_delete)} ${viewModel.observerPlaylist().value?.name}?")
            .setNegativeButton(getString(R.string.fragment_playlist_new_dialog_nevative)) { _, _ -> }
            .setPositiveButton(getString(R.string.fragment_playlist_new_dialog_positive)) { _, _ ->
                viewModel.delete()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun share() {
        if (adapterPlaylistContent.foundTracks.size == 0) {
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_playlist_new_dialog_toast),
                Toast.LENGTH_LONG
            ).show()
        } else {
            startActivity(viewModel.generateIntent())
        }
    }

}