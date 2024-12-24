package com.practicum.playlistmaker.sharing.data.impl

import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel.Companion.MAIL_TO
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl: ExternalNavigator {

    override fun openEmail(emailData: EmailData): Intent {

        val intentEmail = Intent(Intent.ACTION_SENDTO)
        intentEmail.data = Uri.parse(MAIL_TO)
        intentEmail.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.myEmail))
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailData.emailSubject)
        intentEmail.putExtra(Intent.EXTRA_TEXT, emailData.emailText)
        intentEmail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return intentEmail
    }

    override fun openLink(link: String): Intent {

        val intentBrowser = Intent(Intent.ACTION_VIEW)
        intentBrowser.data = Uri.parse(link)
        intentBrowser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return intentBrowser
    }

    override fun shareLink(link: String): Intent {

        val intentShare = Intent(Intent.ACTION_SEND)
        intentShare.putExtra(Intent.EXTRA_TEXT, link)
        intentShare.setType("text/plain")
        intentShare.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return intentShare
    }


}