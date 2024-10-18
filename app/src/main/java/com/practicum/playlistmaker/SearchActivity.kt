package com.practicum.playlistmaker


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {

    //с помощью retrofit создаём сервис Айтюнс
    private val itunesService = ItunesRetrofit.getService()

    //Массив треков для вывода в RecycleView
    private val trackArray = ArrayList<Track>()

    //Инициализация адаптера
    private val adapter = TrackAdapter(trackArray)

    private lateinit var binding: ActivitySearchBinding

    private var lastQuery: String = ""

    private var searchValue: String = SEARCH_FIELD_DEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(SETTINGS, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)
        val adapterHistory = SearchHistoryAdapter(searchHistory.get())

        binding.viewTrackRecycleView.layoutManager = LinearLayoutManager(this)
        binding.viewTrackRecycleView.adapter = adapter
        binding.viewTrackHistoryRecycleView.adapter = adapterHistory
        binding.viewTrackHistoryRecycleView.layoutManager = LinearLayoutManager(this)

        binding.searchClear.isVisible = false

        //Слушатель нажатия кнопки ОК при поиске
        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.searchField.text.isNotEmpty()) {
                    search(binding.searchField.text.toString())
                }
            }
            false
        }

        //Слушатель нажатия на Очистить поле поиска
        binding.searchClear.setOnClickListener {
            binding.searchField.setText(SEARCH_FIELD_DEF)
            clearAll()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }

        //Слушатель нажатия Обновить при ошибке
        binding.searchUpdBttn.setOnClickListener {
            search(lastQuery)
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
                if (binding.searchField.text.isEmpty()) {
                    clearAll()
                    showHistory(adapterHistory)
                }
            }
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)

        //Слушатель при фокусировке на поле поиска
        binding.searchField.setOnFocusChangeListener { view, hasFocus ->

            binding.searchHistory.visibility = if (
                hasFocus
                && binding.searchField.text.isEmpty()
                && adapter.itemCount == 0
                && adapterHistory.itemCount != 0
            ) View.VISIBLE else View.GONE

        }

        //Слушатель нажатия на кнопку очистки истории
        binding.searchClearHistory.setOnClickListener{
            searchHistory.clear()
            adapterHistory.notifyDataSetChanged()
            binding.searchHistory.visibility = View.GONE
        }

        adapter.onClick = { item ->
            Log.i("TEST", "${item.trackName} ${item.artistName} ")

            searchHistory.save(item)
            adapterHistory.notifyDataSetChanged()

        }

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

    private fun search(query: String) {

        binding.searchHistory.visibility = View.GONE

        lastQuery = query
        clearAll()

        itunesService.getTrack("song", query)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    binding.searchHistory.visibility = View.GONE
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.tracksList!!.isNotEmpty()) {
                                trackArray.addAll(response.body()?.tracksList!!)
                                adapter.notifyDataSetChanged()
                            } else {
                                binding.searchNotFound.visibility = View.VISIBLE
                            }

                        }

                        else -> {
                            binding.searchNoConnect.visibility = View.VISIBLE
                            Toast.makeText(
                                applicationContext,
                                response.code().toString(),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    binding.searchNoConnect.visibility = View.VISIBLE
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
            )
    }

    private fun showHistory(adapterHistory: SearchHistoryAdapter){
        if (adapterHistory.itemCount == 0) {
            return
        } else {
            binding.searchHistory.visibility = View.VISIBLE
        }
    }

    private fun clearAll() {
        trackArray.clear()
        adapter.notifyDataSetChanged()
        binding.searchNotFound.visibility = View.GONE
        binding.searchNoConnect.visibility = View.GONE
    }

    companion object {
        const val SEARCH_FIELD_TEXT = "SEARCH_FIELD_TEXT"
        const val SEARCH_FIELD_DEF = ""
    }


}
