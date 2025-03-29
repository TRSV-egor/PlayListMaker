package com.practicum.playlistmaker.media.ui.adapters

import android.net.Uri
import android.os.Environment
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ViewPlaylistBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.media.ui.fragments.NewPlaylistFragment
import java.io.File
import androidx.core.net.toUri


class PlaylistViewHolder(
    private val binding: ViewPlaylistBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: PlaylistModel) {

        with(binding) {

            //TODO каким то макаром вытащить пикчу, по URI
            placeholder.setImageURI(model.path.toUri())
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
