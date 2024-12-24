package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun openSupport() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun shareApp() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {

    }

    private fun getTermsLink(): EmailData {

    }

    private fun getSupportEmailData(): String {

    }

}