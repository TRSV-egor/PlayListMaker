package com.practicum.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewTrackBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackViewHolder

class FavoriteAdapter ( var onClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    var favoriteTracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ViewTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(favoriteTracks[position])
        holder.itemView.setOnClickListener { onClick(favoriteTracks[position]) }
    }

    override fun getItemCount(): Int {
        return favoriteTracks.size
    }

}