package com.practicum.playlistmaker.settings.ui.model


sealed interface IntentType {

    var isLaunched: Boolean

    data class ShareApp(val link: String) : IntentType {
        override var isLaunched: Boolean = false
    }

    data class GetHelp(val emailData: EmailData) : IntentType {
        override var isLaunched: Boolean = false
    }

    data class UserAgreement(val link: String) : IntentType {
        override var isLaunched: Boolean = false
    }

}