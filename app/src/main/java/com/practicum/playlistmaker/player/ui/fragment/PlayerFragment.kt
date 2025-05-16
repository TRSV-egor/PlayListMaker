package com.practicum.playlistmaker.player.ui.fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.player.service.AudioPlayerService
import com.practicum.playlistmaker.player.ui.adapters.BottomsheetPlaylistAdapter
import com.practicum.playlistmaker.player.ui.models.PlayerStatus
import com.practicum.playlistmaker.player.ui.models.PlaylistStatus
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.dpToPx
import com.practicum.playlistmaker.search.ui.fragment.SearchFragment.Companion.TRACK_BUNDLE
import com.practicum.playlistmaker.util.GetCoverArtworkLink
import com.practicum.playlistmaker.util.NetworkBroadcastReciever
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    //region Переменные
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val AUDIOPLAYER_IMAGE_RESOLUTION = 512
        private const val AUDIOPLAYER_IMAGE_ROUNDED_CORNER = 8f
        private const val DESCRIPTION_YEAR_VALUE_INDEX_START = 0
        private const val DESCRIPTION_YEAR_VALUE_INDEX_END = 4
        const val TRACK_PREVIEW_URL = "previewUrl"

        @JvmStatic
        fun newInstance(track: Track) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TRACK_BUNDLE, track)
                }
            }
    }

    private lateinit var track: Track

    private var isClickAllowed = true

    private val audioPlayerViewModel: AudioPlayerViewModel by viewModel<AudioPlayerViewModel>()

    private var adapterPlaylists = BottomsheetPlaylistAdapter { item ->
        onPlaylistClick(item)
    }

    private val networkBroadcastReciever = NetworkBroadcastReciever()

    private lateinit var binding: FragmentPlayerBinding

    //endregion

    //region Bottom sheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    //endregion

    //region FireBase
    private lateinit var analytics: FirebaseAnalytics
    //endregion

    //region Сервис
    private var audioPlayerService: AudioPlayerService? = null

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.AudioPlayerServiceBinder
            audioPlayerService = binder.getService()

            lifecycleScope.launch {
                audioPlayerService?.playerState?.collect {
                    render(it)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlayerService = null
        }
    }
    //endregion


    //region Жизненный цикл
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = FirebaseAnalytics.getInstance(requireContext())
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                audioPlayerViewModel.setTrack(it.getParcelable(TRACK_BUNDLE, Track::class.java)!!)
            } else {
                audioPlayerViewModel.setTrack(it.getParcelable(TRACK_BUNDLE)!!)
            }
        }
        track = audioPlayerViewModel.observerTrack().value as Track
        bindMusicService()
    }

    override fun onResume() {
        super.onResume()

        audioPlayerService?.stopNotification()

        ContextCompat.registerReceiver(
            requireContext(),
            networkBroadcastReciever,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        if (audioPlayerService?.getStatus() != null) {
            when (audioPlayerService?.getStatus()) {
                is PlayerStatus.Default -> binding.customButtonPlay.defaultState()
                is PlayerStatus.Prepared, PlayerStatus.Paused -> binding.customButtonPlay.preparedOrPausedState()
                is PlayerStatus.Playing -> binding.customButtonPlay.playingState()
                else -> {}
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

        audioPlayerViewModel.observerTrack().observe(viewLifecycleOwner) {
            fillPlayer()
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

        binding.customButtonPlay.setOnTouchListener { view, event ->
            view.performClick()
            audioPlayerService?.playbackControl()
            false
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

            analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundleOf(TRACK_BUNDLE to track))

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            audioPlayerViewModel.getPlaylists()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

    }

    override fun onPause() {
        super.onPause()

        if (audioPlayerService?.getStatus() is PlayerStatus.Playing) {
            audioPlayerService?.startNotification("${track.artistName} - ${track.trackName}")
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unbindMusicService()
        requireContext().unregisterReceiver(networkBroadcastReciever)
    }
    //endregion

    //region Служебные
    private fun render(status: PlayerStatus) {
        when (status) {
            is PlayerStatus.Default -> default()
            is PlayerStatus.Prepared -> prepared(status.isTrackCompleted)
            is PlayerStatus.Paused -> paused()
            is PlayerStatus.Playing -> playing(status.timer)
        }
    }

    private fun fillPlayer() {
        audioPlayerViewModel.checkFavoriteStatus(track)
        binding.buttonFavorites.isEnabled = true
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

            trackTimer.text = PlayerStatus.ZERO_TIMER

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

    private fun default() {
        binding.customButtonPlay.defaultState()
        binding.customButtonPlay.isEnabled = false
    }

    private fun prepared(isTrackCompleted: Boolean) {
        binding.customButtonPlay.preparedOrPausedState()
        binding.customButtonPlay.isEnabled = true
        if (isTrackCompleted) {
            binding.trackTimer.text = PlayerStatus.ZERO_TIMER
            audioPlayerService?.stopNotification()
        }
    }

    private fun paused() {
        binding.customButtonPlay.isEnabled = true
    }

    private fun playing(timer: String) {
        binding.customButtonPlay.isEnabled = true
        binding.trackTimer.text = timer
    }

    private fun onPlaylistClick(playlistModel: PlaylistModel) {
        audioPlayerViewModel.addTrackToPlaylist(track, playlistModel)
    }

    private fun bindMusicService() {
        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra(TRACK_PREVIEW_URL, track.previewUrl)
        }

        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(
                requireContext(),
                "Не предоставлены права на уведомления!",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
    //endregion

}