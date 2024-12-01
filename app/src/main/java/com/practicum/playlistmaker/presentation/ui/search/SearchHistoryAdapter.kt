package com.practicum.playlistmaker.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.databinding.ViewTrackBinding

class SearchHistoryAdapter(
    private val historyTrackList: List<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    var onClick: (Track) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ViewTrackBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(historyTrackList[position])
        holder.itemView.setOnClickListener { onClick(historyTrackList[position]) }
    }

    override fun getItemCount(): Int {
        return historyTrackList.size
    }
}