package com.practicum.playlistmaker.media.ui.adapters

import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewPlaylistBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel


class FullscreenPlaylistViewHolder(
    private val binding: ViewPlaylistBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: PlaylistModel) {

        with(binding) {

            if (model.path != "") {
                placeholder.setImageURI(model.path.toUri())
            }

            name.text = model.name
            //TODO склонения проверить
            count.text = "${model.tracks.size} ${declination(model.tracks.size)}"
        }
    }

    private fun declination(number: Int): String {

        if (number in 11..19) {
            return "треков"
        }

        return when (number % 10) {
            1 -> "трек"
            2, 3, 4 -> "трека"
            else -> "треков"
        }
    }
}


