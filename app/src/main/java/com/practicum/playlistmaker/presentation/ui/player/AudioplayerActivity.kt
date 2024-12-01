package com.practicum.playlistmaker.presentation.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.presentation.presenters.mapper.DateFormater
import com.practicum.playlistmaker.presentation.presenters.mapper.GetCoverArtworkLink
import com.practicum.playlistmaker.presentation.ui.search.TRACK_BUNDLE
import com.practicum.playlistmaker.presentation.ui.search.dpToPx

class AudioplayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val TIMER_TRACK_DURATION = 30000L
        private const val TIMER_UPD = 500L
    }

    private lateinit var binding: ActivityAudioplayerBinding
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    val handler = Handler(Looper.getMainLooper())

    override fun onPause() {
        super.onPause()
        pauseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val track = intent.getSerializableExtra(TRACK_BUNDLE) as Track

        fillPlayer(track)
        prepareMediaPlayer(track)

        binding.buttonPlay.setOnClickListener {
            playbackControl()
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

    private fun fillPlayer(item: Track) {

        with(binding) {
            Glide.with(trackImage.context)
                .load(GetCoverArtworkLink.getCoverArtworkLink(item.artworkUrl100, 512))
                .placeholder(R.drawable.track_placeholder)
                .transform(RoundedCorners(binding.trackImage.context.dpToPx(8f)))
                .into(binding.trackImage)

            trackTimer.text = DateFormater.mmSS(TIMER_TRACK_DURATION)

            trackArtist.text = item.artistName
            trackName.text = item.trackName

            descriptionDurationValue.text = item.trackTime
            descriptionAlbumValue.text = item.collectionName
            descriptionYearValue.text = item.releaseDate.substring(0, 4)
            descriptionStyleValue.text = item.primaryGenreName
            descriptionCountryValue.text = item.country
        }


    }

    private fun prepareMediaPlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_play)
            playerState = STATE_PREPARED
            binding.trackTimer.text = "00:00"
        }
    }

    private fun startMediaPlayer() {
        mediaPlayer.start()
        binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_pause)
        playerState = STATE_PLAYING
        handler.post(durationTimer())
    }

    private fun pauseMediaPlayer() {
        mediaPlayer.pause()
        binding.buttonPlay.background = getDrawable(R.drawable.audioplayer_play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pauseMediaPlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startMediaPlayer()
            }
        }
    }

    private fun durationTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    binding.trackTimer.setText(
                        DateFormater.mmSS(mediaPlayer.currentPosition)
                    )
                    handler.postDelayed(this, TIMER_UPD)
                } else {
                    handler.removeCallbacks(this)
                }


            }

        }

    }

}
