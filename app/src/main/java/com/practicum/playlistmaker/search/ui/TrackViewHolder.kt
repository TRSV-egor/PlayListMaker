package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ViewTrackBinding

class TrackViewHolder(
    private val binding: ViewTrackBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {

        with(binding) {
            Glide.with(viewTrackLogo.context)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.track_placeholder)
                .transform(RoundedCorners(viewTrackLogo.context.dpToPx(2f)))
                .into(viewTrackLogo)
            trackName.text = model.trackName
            trackArtist.text = model.artistName
            viewTrackLength.text = model.trackTime
        }
    }
}

fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    ).toInt()
}