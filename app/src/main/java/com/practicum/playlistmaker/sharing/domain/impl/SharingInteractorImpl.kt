package com.practicum.playlistmaker.sharing.domain.impl

import android.content.Intent
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp(courseUrl: String): Intent {
        return externalNavigator.shareLink(courseUrl)
    }

    override fun openSupport(subject: String, text: String, mail: String): Intent {

        return externalNavigator.openEmail(
            EmailData(
                emailText = text,
                myEmail = mail,
                emailSubject = subject,
            )
        )
    }

    override fun openTerms(practicumOffer: String): Intent {
        return externalNavigator.openLink(practicumOffer)
    }

}