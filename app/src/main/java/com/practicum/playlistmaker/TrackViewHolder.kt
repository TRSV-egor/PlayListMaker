package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    private val trackLogo: ImageView = itemview.findViewById(R.id.view_track_logo)
    private val trackName: TextView = itemview.findViewById(R.id.track_name)
    private val trackArtist: TextView = itemview.findViewById(R.id.track_artist)
    private val trackLength: TextView = itemview.findViewById(R.id.view_track_length)

    fun bind(model: Track) {

        Glide.with(trackLogo.context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_placeholder)
            .transform(RoundedCorners(trackLogo.context.dpToPx(2f)))
            .into(trackLogo)
        trackName.text = model.trackName
        trackArtist.text = model.artistName
        trackLength.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime.toLongOrNull())

    }
}

fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    ).toInt()
}