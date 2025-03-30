package com.practicum.playlistmaker.media.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewPlaylistBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel

class FullscreenPlaylistAdapter(
    var onClick: (PlaylistModel) -> Unit
) : RecyclerView.Adapter<FullscreenPlaylistViewHolder>() {

    var playlistList = mutableListOf<PlaylistModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FullscreenPlaylistViewHolder {
        val binding =
            ViewPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FullscreenPlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FullscreenPlaylistViewHolder, position: Int) {
        holder.bind(playlistList[position])
        holder.itemView.setOnClickListener {
            onClick(playlistList[position])
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

}