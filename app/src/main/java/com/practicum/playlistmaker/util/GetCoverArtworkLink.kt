package com.practicum.playlistmaker.util

object GetCoverArtworkLink {
    fun getCoverArtworkLink(link: String, resolution: Int): String {
        return link.replaceAfterLast('/', "${resolution}x${resolution}bb.jpg")
    }
}