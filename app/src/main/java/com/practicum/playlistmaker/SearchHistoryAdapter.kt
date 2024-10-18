package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter(
    private val historyTrackList: ArrayList<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    var onClick: (Track) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(historyTrackList[position])
        holder.itemView.setOnClickListener { onClick(historyTrackList[position]) }
    }

    override fun getItemCount(): Int {
        return historyTrackList.size
    }
}