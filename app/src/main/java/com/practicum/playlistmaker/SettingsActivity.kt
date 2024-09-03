package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setTitle(R.string.settings_name)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarStyle)
        toolbar.setNavigationIcon(R.drawable.toolbar_arrowback)
        toolbar.setNavigationOnClickListener { finish() }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Share app button
        val buttonShare = findViewById<TextView>(R.id.share)

        buttonShare.setOnClickListener{
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.putExtra(Intent.EXTRA_TEXT, R.string.course_url)
            intentShare.setType("text/plain");
            startActivity(intentShare)
        }

        //Support button
        val buttonSupport = findViewById<TextView>(R.id.support)

        buttonSupport.setOnClickListener{
            val myEmail = getString(R.string.my_email)
            val emailSubject = getString(R.string.email_subject)
            val emailText = getString(R.string.email_text)

            val intentEmail = Intent(Intent.ACTION_SENDTO)
            intentEmail.data = Uri.parse("mailto:")
            intentEmail.putExtra(Intent.EXTRA_EMAIL, arrayOf(myEmail))
            intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            intentEmail.putExtra(Intent.EXTRA_TEXT, emailText)
            startActivity(intentEmail)
        }

        //User Agreement button
        val buttonUserAgreement = findViewById<TextView>(R.id.user_agreement)

        buttonUserAgreement.setOnClickListener {
            val intentBrowser = Intent(Intent.ACTION_VIEW)
            intentBrowser.data = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            startActivity(intentBrowser)
        }
    }
}