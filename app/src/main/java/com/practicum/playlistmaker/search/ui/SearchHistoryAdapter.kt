package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewTrackBinding
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryAdapter(
    var onClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    var historyTracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ViewTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(historyTracks[position])
        holder.itemView.setOnClickListener { onClick(historyTracks[position]) }
    }

    override fun getItemCount(): Int {
        return historyTracks.size
    }

}