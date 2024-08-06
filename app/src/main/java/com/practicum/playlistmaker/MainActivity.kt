package com.practicum.playlistmaker

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
        val buttonSearchClickListener : View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Pressed Search", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSearch.setOnClickListener(buttonSearchClickListener)

        //Button Media
        val buttonMedia = findViewById<Button>(R.id.media)

        buttonMedia.setOnClickListener{
            Toast.makeText(this@MainActivity, "Pressed Media", Toast.LENGTH_SHORT).show()
        }

        //Button Settings
        val buttonSettings = findViewById<Button>(R.id.settings)

        val buttonSettingsClickListener : View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Pressed Settings", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSettings.setOnClickListener(buttonSettingsClickListener)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}