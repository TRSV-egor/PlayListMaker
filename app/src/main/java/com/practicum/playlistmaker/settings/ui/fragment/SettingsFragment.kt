package com.practicum.playlistmaker.settings.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.compose.SettingsScreen
import com.practicum.playlistmaker.settings.ui.model.EmailData
import com.practicum.playlistmaker.settings.ui.model.IntentType
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen(settingsViewModel)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.intentLiveData.observe(viewLifecycleOwner) {
            openActivity(it)
        }

        (requireContext().applicationContext as App).checkTheme()
        settingsViewModel.checkDarkTheme((requireContext().applicationContext as App).darkTheme)

    }

    private fun generateIntent(intentType: IntentType): Intent {
        when (intentType) {
            is IntentType.ShareApp -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, intentType.link)
                intent.setType("text/plain")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }

            is IntentType.GetHelp -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse(MAIL_TO)
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(intentType.emailData.email))
                intent.putExtra(Intent.EXTRA_SUBJECT, intentType.emailData.subject)
                intent.putExtra(Intent.EXTRA_TEXT, intentType.emailData.text)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }

            is IntentType.UserAgreement -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(intentType.link)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }
        }
    }

    private fun openActivity(receivedIntent: IntentType) {
        if (!receivedIntent.isLaunched) {
            startActivity(generateIntent(receivedIntent))
            settingsViewModel.changeIntentStatus()
        }
    }

    companion object {
        const val MAIL_TO = "mailto:"
    }

}