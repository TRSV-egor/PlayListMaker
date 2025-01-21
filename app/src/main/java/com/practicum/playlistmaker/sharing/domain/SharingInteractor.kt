package com.practicum.playlistmaker.sharing.domain

import android.content.Intent

interface SharingInteractor {
    fun shareApp(courseUrl: String): Intent
    fun openTerms(practicumOffer: String): Intent
    fun openSupport(subject: String, text: String, mail: String): Intent
}