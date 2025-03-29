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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentPlaylistNewBinding
import com.practicum.playlistmaker.media.domain.model.PlaylistModel
import com.practicum.playlistmaker.media.ui.view_model.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistNewBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val PLAYLIST_ALBUM_PIC_FOLDER = "playlist_album_pic"
    }

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var imageIsLoaded = false
    private var imageUri: String = ""

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

        //ОБрабатываем нажатие аппаратного назад
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dialogBeforeExit()
            }
        })

        binding.arrowBack.setOnClickListener {
            dialogBeforeExit()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.placeholder.setImageURI(uri)
                    imageIsLoaded = true
                    imageUri = uri.toString()
                    //saveImageToPrivateStorage(uri)
                } else {
                    //TODO что-то с этим сделать
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.placeholder.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonSave.setOnClickListener {
            saveAndExit(binding.nameField.text.toString())
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

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

    private fun saveAndExit(fileName: String) {

        if (imageUri.isNotEmpty()) {
            saveImageToPrivateStorage(imageUri.toUri(), fileName)
        }

        showToast(fileName)

        viewModel.save(
            PlaylistModel(
                tracks = listOf(),
                name = binding.nameField.text.toString(),
                path = imageUri,
                description = binding.descriptionField.text.toString()
            )
        )

        findNavController().navigateUp()
    }

    private fun showToast(fileName: String) {
        Toast.makeText(requireContext(), "Плейлист $fileName создан", Toast.LENGTH_SHORT).show()
    }

    private fun saveImageToPrivateStorage(uri: Uri, fileName: String) {
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
        val file = File(filePath, "$fileName.jpg")
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
                .setNegativeButton("Отмена") { dialog, which -> }
                .setPositiveButton("Завершить") { dialog, which ->
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }
}
