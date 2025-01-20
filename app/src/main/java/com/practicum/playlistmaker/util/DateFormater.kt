package com.practicum.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale

object DateFormater{
    fun mmSS(string: Int): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(string)
    }

    fun mmSS(string: Long): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(string)
    }

}