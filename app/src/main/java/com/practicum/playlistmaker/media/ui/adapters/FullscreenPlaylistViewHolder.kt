package com.practicum.playlistmaker.media.ui.adapters

import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewPlaylistBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.util.Declination


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
            count.text = "${model.tracks.size} ${Declination.getTracks(model.tracks.size)}"
        }
    }
}


