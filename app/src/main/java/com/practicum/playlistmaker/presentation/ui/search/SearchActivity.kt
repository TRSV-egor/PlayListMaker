package com.practicum.playlistmaker.presentation.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator.provideTracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.presentation.ui.player.AudioplayerActivity
import java.io.Serializable

const val TRACK_BUNDLE = "track"
const val DEF_SEARCH = "song"

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_FIELD_TEXT = "SEARCH_FIELD_TEXT"
        const val SEARCH_FIELD_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    //Массив треков для вывода в RecycleView
    private val foundTracksArray = ArrayList<Track>()

    //Массив треков для вывода в RecycleView
    private val historyTracksArray = ArrayList<Track>()

    //Инициализация адаптера найденых треков
    private val adapterFound = TrackAdapter(foundTracksArray)

    //Инициализация адаптера истории треков
    private val adapterHistory = SearchHistoryAdapter(historyTracksArray)

    //Инициализация binding
    private lateinit var binding: ActivitySearchBinding

    //Последний поисковый запрос
    private var lastQuery: String = ""

    //Значение поиска по-умолчанию
    private var searchValue: String = SEARCH_FIELD_DEF

    //Инициализация Handler к главному потоку
    private val handler = Handler(Looper.getMainLooper())

    private val tracksInteractor = provideTracksInteractor()

    //Выносим search() в отдельный поток
    private val searchRunnable = Runnable {
        //BUGFIX при удалении текста backspace'ом из поля ввода происходит пустой поиск из за postDelayed
        if (binding.searchField.text.isNotEmpty()) {
            //search()
            clearAll()
            showProgressBar()
            tracksInteractor.searchTracks(DEF_SEARCH, binding.searchField.text.toString(), object : TracksInteractor.TracksConsumer{
                override fun consume(foundTracks: List<Track>) {
                    handler.post{
                        foundTracksArray.addAll(foundTracks)
                        hideAll()
                        adapterFound.notifyDataSetChanged()
                    }
                }
            })
        }

    }

    //Разрешено ли нажатие на позицию в recycler view
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.viewTrackFoundRecycleView.layoutManager = LinearLayoutManager(this)
        binding.viewTrackFoundRecycleView.adapter = adapterFound

        binding.viewTrackHistoryRecycleView.layoutManager = LinearLayoutManager(this)
        binding.viewTrackHistoryRecycleView.adapter = adapterHistory

        binding.searchClear.isVisible = false


        //Слушатель нажатия на Очистить поле поиска
        binding.searchClear.setOnClickListener {
            binding.searchField.setText(SEARCH_FIELD_DEF)
            showHistory(adapterHistory)
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

        //мониторинг изменения в поле ввода
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
                    //showHistory(adapterHistory)
                } else {
                    searchDebounce()
                }
            }
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)

        //Слушатель при фокусировке на поле поиска
        binding.searchField.setOnFocusChangeListener { view, hasFocus ->

            getHistory()

            binding.searchHistory.visibility = if (
                hasFocus
                && binding.searchField.text.isEmpty()
                && adapterFound.itemCount == 0
                && adapterHistory.itemCount != 0
            ) View.VISIBLE else View.GONE

        }

        //Слушатель нажатия на кнопку очистки истории
        binding.searchClearHistory.setOnClickListener {
            clearHistroy()
        }

        //Слшуатель нажатия на элемент recycler view
        adapterFound.onClick = { item ->
            if (clickDebounce()) {
                openAudioplayer(item)
            }
        }

        adapterHistory.onClick = {
           item -> openAudioplayer(item)
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

    private fun clearHistroy(){
        tracksInteractor.clearTrackHistory()
        historyTracksArray.clear()
        adapterFound.notifyDataSetChanged()
        binding.searchHistory.visibility = View.GONE
    }

    private fun getHistory() {
        tracksInteractor.getTracksHistory(object : TracksInteractor.TracksConsumer{
            override fun consume(foundTracks: List<Track>) {
                handler.post{
                    historyTracksArray.addAll(foundTracks)
                    adapterFound.notifyDataSetChanged()
                }


            }
        })
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)

    }

    private fun hideAll() {
        binding.searchHistory.visibility = View.GONE
        binding.searchNoConnect.visibility = View.GONE
        binding.searchNotFound.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.searchHistory.visibility = View.GONE
        binding.searchNoConnect.visibility = View.GONE
        binding.searchNotFound.visibility = View.GONE
        binding.searchProgressBar.visibility = View.VISIBLE
    }
    private fun showNoConnect() {
        binding.searchHistory.visibility = View.GONE
        binding.searchNoConnect.visibility = View.VISIBLE
        binding.searchNotFound.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE
    }
    private fun showNotFound() {
        binding.searchHistory.visibility = View.GONE
        binding.searchNoConnect.visibility = View.GONE
        binding.searchNotFound.visibility = View.VISIBLE
        binding.searchProgressBar.visibility = View.GONE
    }


    private fun showHistory(adapterHistory: SearchHistoryAdapter) {
        if (adapterHistory.itemCount == 0) {
            return
        } else {
            binding.searchHistory.visibility = View.VISIBLE
            binding.searchNoConnect.visibility = View.GONE
            binding.searchNotFound.visibility = View.GONE
            binding.searchProgressBar.visibility = View.GONE
        }
    }

    private fun clearAll() {
        foundTracksArray.clear()
        adapterFound.notifyDataSetChanged()
        showHistory(adapterHistory)
    }

    private fun openAudioplayer(
        item: Track,
        //adapterHistory: SearchHistoryAdapter,
        //searchHistory: SearchHistory
    ) {
        //searchHistory.save(item)
        tracksInteractor.saveTrackToHistory(item)
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

//    private fun search() {
//        handler.postAtFrontOfQueue { showProgressBar() }
//        lastQuery = binding.searchField.text.toString()
//        clearAll()
//
//        itunesService.getTracks("song", binding.searchField.text.toString())
//            .enqueue(object : Callback<TracksSearchResponse> {
//                override fun onResponse(
//                    call: Call<TracksSearchResponse>,
//                    response: Response<TracksSearchResponse>
//                ) {
//                    binding.searchHistory.visibility = View.GONE
//                    binding.searchProgressBar.visibility = View.GONE
//                    when (response.code()) {
//                        200 -> {
//                            if (response.body()?.tracksList!!.isNotEmpty()) {
//                                trackFoundArray.addAll(response.body()?.tracksList!!)
//                                adapter.notifyDataSetChanged()
//                            } else {
//                                binding.searchNotFound.visibility = View.VISIBLE
//                            }
//
//                        }
//
//                        else -> {
//                            binding.searchNoConnect.visibility = View.VISIBLE
//                            Toast.makeText(
//                                applicationContext,
//                                response.code().toString(),
//                                Toast.LENGTH_LONG
//                            )
//                                .show()
//                        }
//
//                    }
//                }
//
//                override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
//                    binding.searchProgressBar.visibility = View.GONE
//                    binding.searchNoConnect.visibility = View.VISIBLE
//                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG)
//                        .show()
//                }
//            }
//            )
//    }




}
