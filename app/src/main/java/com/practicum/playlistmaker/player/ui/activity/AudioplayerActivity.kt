package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.util.DateFormater
import com.practicum.playlistmaker.util.GetCoverArtworkLink
import com.practicum.playlistmaker.player.domain.model.PlayerStateType
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.player.ui.view_model.AudioplayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.search.ui.dpToPx


class AudioplayerActivity : AppCompatActivity() {

    companion object {
        private const val AUDIOPLAYER_IMAGE_RESOLUTION = 512
        private const val AUDIOPLAYER_IMAGE_ROUNDED_CORNER = 8f
        private const val TIMER_TRACK_DURATION = 30000L
        private const val DESCRIPTION_YEAR_VALUE_INDEX_START = 0
        private const val DESCRIPTION_YEAR_VALUE_INDEX_END = 4

    }

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var audioplayerViewModel: AudioplayerViewModel


    override fun onPause() {
        super.onPause()
        audioplayerViewModel.pauseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioplayerViewModel.releaseAudioPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        audioplayerViewModel = ViewModelProvider(this)[AudioplayerViewModel::class.java]

        val track = intent.getSerializableExtra(SearchActivity.TRACK_BUNDLE) as Track

        audioplayerViewModel.fillPlayer(track)
        audioplayerViewModel.prepareMediaPlayer(track.previewUrl)

        audioplayerViewModel.playerState.observe(this, Observer {

            when (it) {
                PlayerStateType.PREPARED -> {
                    binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_play)
                    binding.buttonPlay.isEnabled = true
                }

                PlayerStateType.DEFAULT -> {
                    binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_play_not_ready)
                    binding.buttonPlay.isEnabled = false
                }

                PlayerStateType.PLAYING -> {
                    binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_pause)
                    binding.buttonPlay.isEnabled = true
                }

                PlayerStateType.PAUSED -> {
                    binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_play)
                    binding.buttonPlay.isEnabled = true
                }
            }

        })

        audioplayerViewModel.currentTrack.observe(this, Observer {
            with(binding) {
                Glide.with(trackImage.context)
                    .load(
                        GetCoverArtworkLink.getCoverArtworkLink(
                            it.artworkUrl100,
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

                trackArtist.text = it.artistName
                trackName.text = it.trackName

                descriptionDurationValue.text = it.trackTime
                descriptionAlbumValue.text = it.collectionName
                descriptionYearValue.text = it.releaseDate.substring(
                    DESCRIPTION_YEAR_VALUE_INDEX_START,
                    DESCRIPTION_YEAR_VALUE_INDEX_END
                )
                descriptionStyleValue.text = it.primaryGenreName
                descriptionCountryValue.text = it.country
            }
        })

        audioplayerViewModel.playerTimer.observe(this, Observer {
            binding.trackTimer.text = it
        })

        binding.buttonPlay.setOnClickListener {
            audioplayerViewModel.playbackControl()
        }

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        setTitle("")
        toolbar.setNavigationIcon(R.drawable.toolbar_arrowback)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarStyle)
        toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioplayer_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
