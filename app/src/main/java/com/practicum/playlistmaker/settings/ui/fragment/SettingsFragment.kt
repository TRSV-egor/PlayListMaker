package com.practicum.playlistmaker.settings.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.model.EmailData
import com.practicum.playlistmaker.settings.ui.model.IntentType
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.intentLiveData.observe(viewLifecycleOwner) {
            openActivity(it)
        }

        settingsViewModel.darkThemeLive.observe(viewLifecycleOwner) {
            binding.themeSwitcher.isChecked = it
        }

        (requireContext().applicationContext as App).checkTheme()
        settingsViewModel.checkDarkTheme((requireContext().applicationContext as App).darkTheme)


        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (requireActivity().applicationContext as App).switchTheme(isChecked)
            settingsViewModel.toggleTheme(isChecked)
        }


        binding.share.setOnClickListener {
            settingsViewModel.shareApp(
                requireContext().getString(R.string.course_url)
            )
        }

        binding.support.setOnClickListener {
            settingsViewModel.getHelp(
                EmailData(
                    email = requireContext().getString(R.string.my_email),
                    subject = requireContext().getString(R.string.email_subject),
                    text = requireContext().getString(R.string.email_text),
                )
            )
        }

        binding.userAgreement.setOnClickListener {
            settingsViewModel.userAgreement(
                requireContext().getString(R.string.practicum_offer)
            )
        }

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