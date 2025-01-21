package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.model.ReceivedIntent
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

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

        settingsViewModel.checkDarkTheme((application as App).darkTheme)

        binding.themeSwitcher.isChecked = settingsViewModel.darkThemeLive.value ?: false

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (application as App).switchTheme(isChecked)
            settingsViewModel.toggleTheme(isChecked)
        }

        binding.share.setOnClickListener {
            settingsViewModel.share(
                applicationContext.getString(R.string.course_url)
            )
        }

        binding.support.setOnClickListener {
            settingsViewModel.support(
                applicationContext.getString(R.string.email_subject),
                applicationContext.getString(R.string.email_text),
                applicationContext.getString(R.string.my_email),

                )
        }

        binding.userAgreement.setOnClickListener {
            settingsViewModel.agreement(
                applicationContext.getString(R.string.practicum_offer)
            )
        }
    }

    private fun openActivity(intentStatus: ReceivedIntent) {
        if (!intentStatus.isLaunched) {
            startActivity(intentStatus.intent)
            settingsViewModel.changeIntentStatus()
        }
    }


}