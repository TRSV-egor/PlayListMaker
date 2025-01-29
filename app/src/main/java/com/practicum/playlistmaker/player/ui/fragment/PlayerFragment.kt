package com.practicum.playlistmaker.player.ui.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.ui.models.PlayerStatus
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.dpToPx
import com.practicum.playlistmaker.search.ui.fragment.SearchFragment.Companion.TRACK_BUNDLE
import com.practicum.playlistmaker.util.DateFormater
import com.practicum.playlistmaker.util.GetCoverArtworkLink
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var track: Track? = null


    private lateinit var binding: FragmentPlayerBinding
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModel<AudioPlayerViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                track = it.getParcelable(TRACK_BUNDLE, Track::class.java)
            } else {
                track = it.getParcelable(TRACK_BUNDLE)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioPlayerViewModel.fillPlayer(track ?: return)

        audioPlayerViewModel.observerPlayer().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.buttonPlay.setOnClickListener {
            audioPlayerViewModel.playbackControl()
        }
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

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
        binding.buttonPlay.background = requireContext().getDrawable(R.drawable.audioplayer_play_not_ready)
        binding.buttonPlay.isEnabled = false

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
    }

    private fun prepared(isTrackCompleted: Boolean) {
        binding.buttonPlay.background = requireContext().getDrawable(R.drawable.audioplayer_play)
        binding.buttonPlay.isEnabled = true
        if (isTrackCompleted) binding.trackTimer.text = PlayerStatus.ZERO_TIMER

    }

    private fun paused() {
        binding.buttonPlay.background = requireContext().getDrawable(R.drawable.audioplayer_play)
        binding.buttonPlay.isEnabled = true
    }

    private fun playing(timer: String) {
        binding.buttonPlay.background = requireContext().getDrawable(R.drawable.audioplayer_pause)
        binding.buttonPlay.isEnabled = true
        binding.trackTimer.text = timer
    }

    companion object {
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