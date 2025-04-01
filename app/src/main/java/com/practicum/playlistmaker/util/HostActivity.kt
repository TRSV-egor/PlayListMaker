package com.practicum.playlistmaker.util

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityHostBinding

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment -> {
                    binding.bottomNaviLine.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                }
                R.id.newPlaylistFragment -> {
                    binding.bottomNaviLine.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                }
                R.id.mediaFragment -> {
                    binding.bottomNaviLine.visibility = View.VISIBLE
                    binding.toolbar.title = getString(R.string.media_name)
                    binding.toolbar.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.searchFragment -> {
                    binding.bottomNaviLine.visibility = View.VISIBLE
                    binding.toolbar.title = getString(R.string.search_name)
                    binding.toolbar.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.settingsFragment -> {
                    binding.bottomNaviLine.visibility = View.VISIBLE
                    binding.toolbar.title = getString(R.string.settings_name)
                    binding.toolbar.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.playlistFragment -> {
                    binding.bottomNaviLine.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                }

                else -> {
                    binding.bottomNaviLine.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }
}