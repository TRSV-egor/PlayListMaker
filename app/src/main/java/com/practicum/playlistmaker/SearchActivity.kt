package com.practicum.playlistmaker


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ActivitySearchBinding


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var searchValue: String = SEARCH_FIELD_DEF

    override fun onCreate(savedInstanceState: Bundle?) {

        val trackArray = TrackMockObject.getTracks()

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val recyclerView = findViewById<RecyclerView>(R.id.view_track_recycle_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val trackAdapter = TrackAdapter(trackArray)
        recyclerView.adapter = trackAdapter

        binding.searchClear.isVisible = false

        binding.searchClear.setOnClickListener {
            binding.searchField.setText(SEARCH_FIELD_DEF)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchClear.isVisible = clearButtonVisibility(s)
                searchValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.toolbar_arrowback)
        setTitle(R.string.search_name)
        toolbar.setTitleTextAppearance(this, R.style.ToolbarStyle)
        toolbar.setNavigationOnClickListener { finish() }


        ViewCompat.setOnApplyWindowInsetsListener(binding.search) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_TEXT, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchField.setText(
            savedInstanceState.getString(
                SEARCH_FIELD_TEXT,
                SEARCH_FIELD_DEF
            )
        )
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    companion object {
        const val SEARCH_FIELD_TEXT = "SEARCH_FIELD_TEXT"
        const val SEARCH_FIELD_DEF = ""
    }
}
