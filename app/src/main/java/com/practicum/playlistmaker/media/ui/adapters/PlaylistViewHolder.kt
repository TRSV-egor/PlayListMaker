package com.practicum.playlistmaker.media.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewPlaylistBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel


class PlaylistViewHolder(
    private val binding: ViewPlaylistBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: PlaylistModel) {

        with(binding) {

            //TODO каким то макаром вытащить пикчу, по URI
            //placeholder = model.path
            name.text = model.name
            //TODO склонения
            count.text = "${model.tracks.size} трек"

        }
    }
}

//    fun Context.dpToPx(dp: Float): Int {
//        return TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            dp,
//            this.resources.displayMetrics
//        ).toInt()
//    }
