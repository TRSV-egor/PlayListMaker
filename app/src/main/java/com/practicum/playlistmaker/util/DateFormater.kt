package com.practicum.playlistmaker.util

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale

object DateFormater {
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

    fun toLong(string: String): Long {
        val parts = string.split(":")
        val localTime: LocalTime

        if (parts.size == 2) {
            val minutes = parts[0].toInt()
            val seconds = parts[1].toInt()
            localTime = LocalTime.of(0, minutes, seconds)
            return localTime.toSecondOfDay().toLong() * 1000
        } else {
            throw IllegalArgumentException("Неправильный формат времени. Ожидается 'mm:ss'.")
        }

    }
}

