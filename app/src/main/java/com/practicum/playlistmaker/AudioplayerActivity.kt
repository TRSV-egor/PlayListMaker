package com.practicum.playlistmaker

import android.os.Bundle
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

    private lateinit var binding: ActivityAudioplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        fillPlayer(intent.getSerializableExtra("track") as Track)

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

        Glide.with(binding.trackImage.context)
            .load(getCoverArtworkLink(item.artworkUrl100, 512))
            .placeholder(R.drawable.track_placeholder)
            .transform(RoundedCorners(binding.trackImage.context.dpToPx(8f)))
            .into(binding.trackImage)

        binding.trackTimer.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTime.toLongOrNull())

        binding.trackArtist.text = item.artistName
        binding.trackName.text = item.trackName

        binding.descriptionDurationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTime.toLongOrNull())
        binding.descriptionAlbumValue.text = item.collectionName
        binding.descriptionYearValue.text = item.releaseDate.substring(0, 4)
        binding.descriptionStyleValue.text = item.primaryGenreName
        binding.descriptionCountryValue.text = item.country
    }


    fun getCoverArtworkLink(link: String, resolution: Int): String {
        return link.replaceAfterLast('/', "${resolution}x${resolution}bb.jpg")
    }

}
