package com.practicum.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.player.ui.models.PlayerStatus
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.search.ui.dpToPx
import com.practicum.playlistmaker.util.DateFormater
import com.practicum.playlistmaker.util.GetCoverArtworkLink


class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val AUDIOPLAYER_IMAGE_RESOLUTION = 512
        private const val AUDIOPLAYER_IMAGE_ROUNDED_CORNER = 8f
        private const val TIMER_TRACK_DURATION = 30000L
        private const val DESCRIPTION_YEAR_VALUE_INDEX_START = 0
        private const val DESCRIPTION_YEAR_VALUE_INDEX_END = 4
    }

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var audioPlayerViewModel: AudioPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        audioPlayerViewModel = ViewModelProvider(this)[AudioPlayerViewModel::class.java]

        //val track = intent.getSerializableExtra(SearchActivity.TRACK_BUNDLE) as Track

        audioPlayerViewModel.fillPlayer(intent.getSerializableExtra(SearchActivity.TRACK_BUNDLE) as Track)
        //audioPlayerViewModel.prepareMediaPlayer(track.previewUrl)

        audioPlayerViewModel.observerPlayer().observe(this) {
            render(it)
        }



        binding.buttonPlay.setOnClickListener {
            audioPlayerViewModel.playbackControl()
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

    override fun onPause() {
        super.onPause()
        audioPlayerViewModel.pauseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerViewModel.releaseAudioPlayer()
    }

    private fun render(status: PlayerStatus) {
        when (status) {
            is PlayerStatus.Default -> default(status.track)
            is PlayerStatus.Prepared -> prepeared()
            is PlayerStatus.Paused -> paused()
            is PlayerStatus.Playing -> playing(status.timer)
        }
    }

    private fun default(track: Track) {
        binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_play_not_ready)
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

    private fun prepeared() {
        binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_play)
        binding.buttonPlay.isEnabled = true
    }

    private fun paused() {
        binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_play)
        binding.buttonPlay.isEnabled = true
    }

    private fun playing(timer: String) {
        binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_pause)
        binding.buttonPlay.isEnabled = true
        binding.trackTimer.text = timer
    }


}
