package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

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

    fun fillPlayer(item: Track) {

        with(binding) {
            Glide.with(trackImage.context)
                .load(getCoverArtworkLink(item.artworkUrl100, 512))
                .placeholder(R.drawable.track_placeholder)
                .transform(RoundedCorners(binding.trackImage.context.dpToPx(8f)))
                .into(binding.trackImage)

            trackTimer.text =
                    //    SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTime.toLongOrNull())
                    //Указано время 30000 в millis = 30 секунд
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(TIMER_TRACK_DURATION)

            trackArtist.text = item.artistName
            trackName.text = item.trackName

            descriptionDurationValue.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTime.toLongOrNull())
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

    fun getCoverArtworkLink(link: String, resolution: Int): String {
        return link.replaceAfterLast('/', "${resolution}x${resolution}bb.jpg")
    }

    fun durationTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    binding.trackTimer.setText(
                        SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(mediaPlayer.currentPosition)
                    )
                    handler.postDelayed(this, TIMER_UPD)
                } else {
                    handler.removeCallbacks(this)
                }


            }

        }

    }

}
