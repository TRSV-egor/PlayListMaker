package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

const val TRACK_BUNDLE = "track"

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_FIELD_TEXT = "SEARCH_FIELD_TEXT"
        const val SEARCH_FIELD_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    //с помощью retrofit создаём сервис Айтюнс
    private val itunesService = ItunesRetrofit.getService()

    //Массив треков для вывода в RecycleView
    private val trackArray = ArrayList<Track>()

    //Инициализация адаптера
    private val adapter = TrackAdapter(trackArray)

    //Инициализация binding
    private lateinit var binding: ActivitySearchBinding

    //Последний поисковый запрос
    private var lastQuery: String = ""

    //Значение поиска по-умолчанию
    private var searchValue: String = SEARCH_FIELD_DEF

    //Инициализация Handler к главному потоку
    private val handler = Handler(Looper.getMainLooper())

    //Выносим search() в отдельный поток
    private val searchRunnable = Runnable {
        //BUGFIX при удалении текста backspace'ом из поля ввода происходит пустой поиск из за postDelayed
        if (binding.searchField.text.isNotEmpty()) {
            search()
        }
    }

    //Разрешено ли нажатие на позицию в recycler view
    private var isClickAllowed = true

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

        //Слушатель нажатия на Очистить поле поиска
        binding.searchClear.setOnClickListener {
            binding.searchField.setText(SEARCH_FIELD_DEF)
            clearAll()
            //Сворачиваем клавиатуру
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }

        //Слушатель нажатия кнопки Обновить при ошибке связи
        binding.searchUpdBttn.setOnClickListener {
            showProgressBar()
            binding.searchField.setText(lastQuery)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchClear.isVisible = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.searchField.text.isEmpty()) {
                    clearAll()
                    showHistory(adapterHistory)
                } else {
                    searchDebounce()
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
        binding.searchClearHistory.setOnClickListener {
            searchHistory.clear()
            adapterHistory.notifyDataSetChanged()
            binding.searchHistory.visibility = View.GONE
        }

        //Слшуатель нажатия на элемент recycler view
        adapter.onClick = { item ->
            if (clickDebounce()) {
                openAudioplayer(item, adapterHistory, searchHistory)
            }
        }

        adapterHistory.onClick = { item -> openAudioplayer(item, adapterHistory, searchHistory) }

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

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showProgressBar() {
        binding.searchHistory.visibility = View.GONE
        binding.searchNoConnect.visibility = View.GONE
        binding.searchNotFound.visibility = View.GONE
        binding.searchProgressBar.visibility = View.VISIBLE
    }

    private fun search() {
        handler.postAtFrontOfQueue { showProgressBar() }
        lastQuery = binding.searchField.text.toString()
        clearAll()

        itunesService.getTrack("song", binding.searchField.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    binding.searchHistory.visibility = View.GONE
                    binding.searchProgressBar.visibility = View.GONE
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
                    binding.searchProgressBar.visibility = View.GONE
                    binding.searchNoConnect.visibility = View.VISIBLE
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
            )
    }

    private fun showHistory(adapterHistory: SearchHistoryAdapter) {
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

    private fun openAudioplayer(
        item: Track,
        adapterHistory: SearchHistoryAdapter,
        searchHistory: SearchHistory
    ) {
        searchHistory.save(item)
        adapterHistory.notifyDataSetChanged()
        val intent = Intent(this, AudioplayerActivity::class.java)
        intent.putExtra(TRACK_BUNDLE, item as Serializable)
        startActivity(intent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}
