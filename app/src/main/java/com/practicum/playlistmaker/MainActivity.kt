package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Button Search
        val buttonSearch = findViewById<Button>(R.id.search)
        buttonSearch.setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }

        //Button Media
        val buttonMedia = findViewById<Button>(R.id.media)

        buttonMedia.setOnClickListener{
            startActivity(Intent(this, MediaActivity::class.java))
        }

        //Button Settings
        val buttonSettings = findViewById<Button>(R.id.settings)

        buttonSettings.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}