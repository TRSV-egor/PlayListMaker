package com.practicum.playlistmaker


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    //Задаём базовый URL
    //private var baseURL: String = getString(R.string.itunes_base_url)
    private val baseURL = "https://itunes.apple.com"

    //Инициализируем retrofit передавая базовый URL и GsonConverter
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //с помощью retrofit создаём сервис Айтюнс
    private val itunesService: ItunesAPI = retrofit.create(ItunesAPI::class.java)

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

        binding.viewTrackRecycleView.layoutManager = LinearLayoutManager(this)
        binding.viewTrackRecycleView.adapter = adapter
        binding.searchClear.isVisible = false
        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.searchField.text.isNotEmpty()) {
                    search(binding.searchField.text.toString())
                }
            }
            false
        }

        binding.searchClear.setOnClickListener {
            binding.searchField.setText(SEARCH_FIELD_DEF)
            trackArray.clear()
            adapter.notifyDataSetChanged()
            binding.searchNotFound.visibility = View.GONE
            binding.searchNoConnect.visibility = View.GONE
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
        }

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

    private fun search(query: String) {

        lastQuery = query
        trackArray.clear()

        binding.searchNoConnect.visibility = View.GONE
        binding.searchNotFound.visibility = View.GONE


        itunesService.getTrack("song", query)
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
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

    companion object {
        const val SEARCH_FIELD_TEXT = "SEARCH_FIELD_TEXT"
        const val SEARCH_FIELD_DEF = ""
    }


}
