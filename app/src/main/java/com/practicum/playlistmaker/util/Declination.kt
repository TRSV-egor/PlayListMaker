package com.practicum.playlistmaker.util

object Declination {

    fun getTracks(number: Int): String {

        if (number in 11..19) {
            return "треков"
        }

        return when (number % 10) {
            1 -> "трек"
            2, 3, 4 -> "трека"
            else -> "треков"
        }
    }

    fun getMinutes(number: Int): String {

        if (number in 11..19) {
            return "минут"
        }

        return when (number % 10) {
            1 -> "минута"
            2, 3, 4 -> "минуты"
            else -> "минут"
        }
    }
}