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
import com.practicum.playlistmaker.App

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

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

        //night light theme
        if ((applicationContext as App).darkTheme) {
            binding.themeSwitcher.isChecked = (applicationContext as App).darkTheme
        }
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        //Share app button
        binding.share.setOnClickListener {
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_url))
            intentShare.setType("text/plain")
            startActivity(intentShare)
        }

        //Support button
        binding.support.setOnClickListener {
            val myEmail = getString(R.string.my_email)
            val emailSubject = getString(R.string.email_subject)
            val emailText = getString(R.string.email_text)

            val intentEmail = Intent(Intent.ACTION_SENDTO)
            intentEmail.data = Uri.parse(MAIL_TO)
            intentEmail.putExtra(Intent.EXTRA_EMAIL, arrayOf(myEmail))
            intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            intentEmail.putExtra(Intent.EXTRA_TEXT, emailText)
            startActivity(intentEmail)
        }

        //User Agreement button
        binding.userAgreement.setOnClickListener {
            val intentBrowser = Intent(Intent.ACTION_VIEW)
            intentBrowser.data = Uri.parse(getString(R.string.practicum_offer))
            startActivity(intentBrowser)
        }
    }

    companion object {
        const val MAIL_TO = "mailto:"
    }
}