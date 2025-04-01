package com.practicum.playlistmaker.player.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewPlaylistPopupBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel

class BottomsheetPlaylistAdapter(
    var onClick: (PlaylistModel) -> Unit
) : RecyclerView.Adapter<BottomsheetPlaylistViewHolder>() {

    var playlistList = mutableListOf<PlaylistModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomsheetPlaylistViewHolder {
        val binding =
            ViewPlaylistPopupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BottomsheetPlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomsheetPlaylistViewHolder, position: Int) {
        holder.bind(playlistList[position])
        holder.itemView.setOnClickListener {
            onClick(playlistList[position])
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

}