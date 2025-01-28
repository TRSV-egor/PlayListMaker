package com.practicum.playlistmaker.settings.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.model.EmailData
import com.practicum.playlistmaker.settings.ui.model.IntentType
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val MAIL_TO = "mailto:"
    }

    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.settings_name)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarStyle)
        toolbar.setNavigationIcon(R.drawable.toolbar_arrowback)
        toolbar.setNavigationOnClickListener { finish() }


        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsViewModel.intentLiveData.observe(this) {
            openActivity(it)
        }

        settingsViewModel.darkThemeLive.observe(this) {
            binding.themeSwitcher.isChecked = it
        }

        (application as App).checkTheme()
        settingsViewModel.checkDarkTheme((application as App).darkTheme)


        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (application as App).switchTheme(isChecked)
            settingsViewModel.toggleTheme(isChecked)
        }


        binding.share.setOnClickListener {
            settingsViewModel.shareApp(
                applicationContext.getString(R.string.course_url)
            )
        }

        binding.support.setOnClickListener {
            settingsViewModel.getHelp(
                EmailData(
                    email = applicationContext.getString(R.string.my_email),
                    subject = applicationContext.getString(R.string.email_subject),
                    text = applicationContext.getString(R.string.email_text),
                )
            )
        }

        binding.userAgreement.setOnClickListener {
            settingsViewModel.userAgreement(
                applicationContext.getString(R.string.practicum_offer)
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

}