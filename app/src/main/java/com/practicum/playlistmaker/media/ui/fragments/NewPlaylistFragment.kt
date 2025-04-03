package com.practicum.playlistmaker.media.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
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
import com.practicum.playlistmaker.R
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

    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                viewModel.fillPlaylistEditor(
                    it.getParcelable(
                        PlaylistListFragment.PLAYLIST_BUNDLE,
                        PlaylistModel::class.java
                    )
                )
            } else {
                viewModel.fillPlaylistEditor(it.getParcelable(PlaylistListFragment.PLAYLIST_BUNDLE))
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        backPressedCallback?.remove()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dialogBeforeExit() { isClosed ->
                    if (isClosed) {
                        isEnabled = false
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback!!
        )

        binding.arrowBack.setOnClickListener {
            dialogBeforeExit() { _ -> }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.imageLoaded(uri.toString())
                }
            }

        binding.placeholder.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonSave.setOnClickListener {
            saveAndExit()
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.buttonSave.isEnabled = !s.isNullOrEmpty()
            }
        }

        binding.nameField.addTextChangedListener(simpleTextWatcher)

        viewModel.observeState().observe(viewLifecycleOwner) {
            imageUri = it.first
            imageIsLoaded = it.second

            if (imageIsLoaded) {
                binding.placeholder.setImageURI(imageUri.toUri())
            }
        }

        viewModel.observerEditor().observe(viewLifecycleOwner) {
            if (it == null) {
                binding.buttonSave.text =
                    getString(R.string.fragment_playlist_new_button_description)
            } else {
                binding.header.text = getString(R.string.fragment_playlist_new_header2)
                binding.buttonSave.text =
                    getString(R.string.fragment_playlist_new_button_description2)
                if (it.path != "") binding.placeholder.setImageURI(it.path.toUri())
                binding.name.editText?.setText(it.name)
                binding.description.editText?.setText(it.description)
            }
        }
    }


    private fun saveAndExit() {


        val pathToFile: String = if (imageUri.isNotEmpty()) {
            saveImageToPrivateStorage(
                imageUri.toUri(),
                binding.nameField.text.toString()
            ).toString()
        } else if (viewModel.observerEditor().isInitialized) {
            viewModel.observerEditor().value?.path ?: ""
        } else {
            ""
        }

        if (viewModel.observerEditor().isInitialized) {

            viewModel.update(
                name = binding.nameField.text.toString(),
                path = pathToFile,
                description = binding.descriptionField.text.toString()
            )
            findNavController().navigateUp()
        } else {


            viewModel.save(
                name = binding.nameField.text.toString(),
                path = pathToFile,
                description = binding.descriptionField.text.toString()
            )

            showToast(binding.nameField.text.toString())

            findNavController().navigateUp()
        }


    }

    private fun showToast(fileName: String) {
        Toast.makeText(
            requireContext(),
            getString(R.string.fragment_playlist_new_toast_1) + fileName + getString(R.string.fragment_playlist_new_toast_2),
            Toast.LENGTH_SHORT
        ).show()
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

    private fun dialogBeforeExit(isClosed: (Boolean) -> Unit) {


        if (viewModel.observerEditor().isInitialized) {
            isClosed(false)
            findNavController().navigateUp()
        }

        if (
            !binding.nameField.text.isNullOrEmpty() ||
            !binding.descriptionField.text.isNullOrEmpty() || imageIsLoaded
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.fragment_playlist_new_dialog_title))
                .setMessage(getString(R.string.fragment_playlist_new_dialog_message))
                .setNegativeButton(getString(R.string.fragment_playlist_new_dialog_nevative)) { _, _ ->
                    isClosed(false)
                }
                .setPositiveButton(getString(R.string.fragment_playlist_new_dialog_positive)) { _, _ ->
                    isClosed(true)
                    findNavController().navigateUp()
                }
                .show()
        } else {
            isClosed(false)
            findNavController().navigateUp()
        }

    }
}
