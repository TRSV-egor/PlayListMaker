package com.practicum.playlistmaker.media.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentPlaylistNewBinding
import java.io.File
import java.io.FileOutputStream


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistNewBinding? = null
    private val binding get() = _binding!!

    //private val audioPlayerViewModel: AudioPlayerViewModel by viewModel<AudioPlayerViewModel>()

    companion object {
        fun newInstance() = NewPlaylistFragment()
        const val PLAYLIST_ALBUM_PIC_FOLDER = "playlist_album_pic"
    }

    private var imageIsLoaded = false
//    private lateinit var imageUri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.arrowBack.setOnClickListener {
            dialogBeforeExit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dialogBeforeExit()
            }
        })

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.placeholder.setImageURI(uri)
                    imageIsLoaded = true
                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.placeholder.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.buttonSave.isEnabled = false
                } else {
                    binding.buttonSave.isEnabled = true
                }
            }
        }

        binding.nameField.addTextChangedListener(simpleTextWatcher)

    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLIST_ALBUM_PIC_FOLDER
        )
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        //TODO взять из название

        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun dialogBeforeExit() {

        if (
            !binding.nameField.text.isNullOrEmpty() ||
            !binding.descriptionField.text.isNullOrEmpty() || imageIsLoaded
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?") // Заголовок диалога
                //.setMessage("Описание") // Описание диалога
//            .setNeutralButton("Отмена") { dialog, which -> // Добавляет кнопку «Отмена»
//                // Действия, выполняемые при нажатии на кнопку «Отмена»
//            }
                .setNegativeButton("Отмена") { dialog, which -> // Добавляет кнопку «Нет»
                    // Действия, выполняемые при нажатии на кнопку «Нет»
                }
                .setPositiveButton("Завершить") { dialog, which -> // Добавляет кнопку «Да»
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }


    }

    private fun saveButton() {
        findNavController().navigateUp()
    }
}
