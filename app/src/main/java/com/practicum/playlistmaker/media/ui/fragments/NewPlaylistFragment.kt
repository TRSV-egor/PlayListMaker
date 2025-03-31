package com.practicum.playlistmaker.media.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentPlaylistNewBinding
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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Это вызывает краш приложения при использовании аппаратной кнопки закрытия на других фрагментах
//        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                dialogBeforeExit()
//            }
//        })

        binding.arrowBack.setOnClickListener {
            dialogBeforeExit()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.placeholder.setImageURI(uri)
                    imageIsLoaded = true
                    imageUri = uri.toString()
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

        val pathToFile: String = if (imageUri.isNotEmpty()) {
            saveImageToPrivateStorage(imageUri.toUri(), fileName).toString()
        } else {
            ""
        }

        showToast(fileName)

        viewModel.save(
            name = binding.nameField.text.toString(),
            path = pathToFile,
            description = binding.descriptionField.text.toString()
        )

        findNavController().navigateUp()
    }

    private fun showToast(fileName: String) {
        Toast.makeText(requireContext(), "Плейлист $fileName создан", Toast.LENGTH_SHORT).show()
    }


    private fun saveImageToPrivateStorage(uri: Uri, fileName: String): File {

        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PLAYLIST_ALBUM_PIC_FOLDER
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "$fileName.jpg")

        val inputStream = requireActivity().contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file
    }

    private fun dialogBeforeExit() {

        if (
            !binding.nameField.text.isNullOrEmpty() ||
            !binding.descriptionField.text.isNullOrEmpty() || imageIsLoaded
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
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
