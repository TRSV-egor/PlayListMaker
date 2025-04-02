package com.practicum.playlistmaker.player.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.player.ui.adapters.BottomsheetPlaylistAdapter
import com.practicum.playlistmaker.player.ui.models.PlayerStatus
import com.practicum.playlistmaker.player.ui.models.PlaylistStatus
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.dpToPx
import com.practicum.playlistmaker.search.ui.fragment.SearchFragment.Companion.TRACK_BUNDLE
import com.practicum.playlistmaker.util.DateFormater
import com.practicum.playlistmaker.util.GetCoverArtworkLink
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var track: Track? = null

    private var isClickAllowed = true

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val clickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { allowed ->
            isClickAllowed = allowed
        }

    private lateinit var binding: FragmentPlayerBinding
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModel<AudioPlayerViewModel>()


    private var adapterPlaylists = BottomsheetPlaylistAdapter { item ->
        onPlaylistClick(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                track = it.getParcelable(TRACK_BUNDLE, Track::class.java)
            } else {
                track = it.getParcelable(TRACK_BUNDLE)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
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


        binding.audioplayerRecyclerview.layoutManager =
            LinearLayoutManager(requireContext())
        binding.audioplayerRecyclerview.adapter = adapterPlaylists


        audioPlayerViewModel.observerPlayer().observe(viewLifecycleOwner) {
            render(it)
        }

        audioPlayerViewModel.observePlaylist().observe(viewLifecycleOwner) {
            adapterPlaylists.playlistList.clear()
            adapterPlaylists.playlistList.addAll(it)
            adapterPlaylists.notifyDataSetChanged()
        }

        audioPlayerViewModel.observeMessageStatus().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistStatus.Default -> {}
                is PlaylistStatus.Exist -> {
                    if (it.notify) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is PlaylistStatus.Added -> {
                    if (it.notify) {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    audioPlayerViewModel.messageBeenSend()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        audioPlayerViewModel.observerFavoriteTrack().observe(viewLifecycleOwner) {
            if (it) {
                binding.buttonFavorites.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.audioplayer_favorites_on
                )
            } else {
                binding.buttonFavorites.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.audioplayer_favorites_off
                )
            }
        }


        binding.buttonPlay.setOnClickListener {
            audioPlayerViewModel.playbackControl()
        }
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.mediaButtonNewPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            findNavController().navigate(
                R.id.action_playerFragment_to_newPlaylistFragment,
            )
        }
        binding.buttonPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            audioPlayerViewModel.getPlaylists()
        }


        audioPlayerViewModel.fillPlayer(track ?: return)


    }


    override fun onPause() {
        super.onPause()
        audioPlayerViewModel.pauseMediaPlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioPlayerViewModel.releaseAudioPlayer()
    }

    private fun render(status: PlayerStatus) {
        when (status) {
            is PlayerStatus.Default -> default(status.track)
            is PlayerStatus.Prepared -> prepared(status.isTrackCompleted)
            is PlayerStatus.Paused -> paused()
            is PlayerStatus.Playing -> playing(status.timer)
        }
    }

    private fun default(track: Track) {
        binding.buttonPlay.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.audioplayer_play_not_ready)
        binding.buttonPlay.isEnabled = false

        audioPlayerViewModel.checkFavoriteStatus(track)

        binding.buttonFavorites.setOnClickListener {
            audioPlayerViewModel.changeFavoriteStatus(track)
        }


        with(binding) {
            Glide.with(trackImage.context)
                .load(
                    GetCoverArtworkLink.getCoverArtworkLink(
                        track.artworkUrl100,
                        AUDIOPLAYER_IMAGE_RESOLUTION
                    )
                )
                .placeholder(R.drawable.track_placeholder)
                .transform(
                    RoundedCorners(
                        binding.trackImage.context.dpToPx(
                            AUDIOPLAYER_IMAGE_ROUNDED_CORNER
                        )
                    )
                )
                .into(binding.trackImage)

            trackTimer.text = DateFormater.mmSS(TIMER_TRACK_DURATION)

            trackArtist.text = track.artistName
            trackName.text = track.trackName

            descriptionDurationValue.text = track.trackTime
            descriptionAlbumValue.text = track.collectionName
            descriptionYearValue.text = track.releaseDate.substring(
                DESCRIPTION_YEAR_VALUE_INDEX_START,
                DESCRIPTION_YEAR_VALUE_INDEX_END
            )
            descriptionStyleValue.text = track.primaryGenreName
            descriptionCountryValue.text = track.country
        }

        binding.buttonFavorites.isEnabled = true
    }

    private fun prepared(isTrackCompleted: Boolean) {
        binding.buttonPlay.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.audioplayer_play)
        binding.buttonPlay.isEnabled = true
        if (isTrackCompleted) binding.trackTimer.text = PlayerStatus.ZERO_TIMER

    }

    private fun paused() {
        binding.buttonPlay.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.audioplayer_play)
        binding.buttonPlay.isEnabled = true
    }

    private fun playing(timer: String) {
        binding.buttonPlay.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.audioplayer_pause)
        binding.buttonPlay.isEnabled = true
        binding.trackTimer.text = timer
    }

    private fun onPlaylistClick(playlistModel: PlaylistModel) {
        track?.let { audioPlayerViewModel.addTrackToPlaylist(it, playlistModel) }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickDebounce(true)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val AUDIOPLAYER_IMAGE_RESOLUTION = 512
        private const val AUDIOPLAYER_IMAGE_ROUNDED_CORNER = 8f
        private const val TIMER_TRACK_DURATION = 30000L
        private const val DESCRIPTION_YEAR_VALUE_INDEX_START = 0
        private const val DESCRIPTION_YEAR_VALUE_INDEX_END = 4

        @JvmStatic
        fun newInstance(track: Track) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TRACK_BUNDLE, track)
                }
            }
    }
}