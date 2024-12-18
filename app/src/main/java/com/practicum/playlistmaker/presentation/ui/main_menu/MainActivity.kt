package com.practicum.playlistmaker.presentation.ui.main_menu

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.presentation.ui.media.MediaActivity
import com.practicum.playlistmaker.presentation.ui.search.SearchActivity
import com.practicum.playlistmaker.presentation.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Button Search
        binding.search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        //Button Media
        binding.media.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        //Button Settings
        binding.settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}