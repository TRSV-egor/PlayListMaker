package com.practicum.playlistmaker.sharing.domain.impl

import android.app.Application
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private var application: Application
) : SharingInteractor {

    override fun openSupport() {
        application.startActivity(externalNavigator.shareLink(getShareAppLink()))
    }

    override fun openTerms() {
        application.startActivity(externalNavigator.openLink(getTermsLink()))
    }

    override fun shareApp() {
        application.startActivity(externalNavigator.openEmail(getSupportEmailData()))
    }

    private fun getShareAppLink(): String {
        return application.getString(R.string.course_url)
    }

    private fun getTermsLink(): String {
        return application.getString(R.string.practicum_offer)
    }

    private fun getSupportEmailData(): EmailData {

        return EmailData(
            emailSubject = application.getString(R.string.email_subject),
            emailText = application.getString(R.string.email_text),
            myEmail = application.getString(R.string.my_email),
        )
    }

}