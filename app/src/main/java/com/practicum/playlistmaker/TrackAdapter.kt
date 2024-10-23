package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewTrackBinding

class TrackAdapter(
    private val trackList: ArrayList<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    var onClick: (Track) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ViewTrackBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener { onClick(trackList[position]) }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}