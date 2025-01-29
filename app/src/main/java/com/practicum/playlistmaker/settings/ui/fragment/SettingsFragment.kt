package com.practicum.playlistmaker.settings.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.model.EmailData
import com.practicum.playlistmaker.settings.ui.model.IntentType
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.App
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val toolbar: androidx.appcompat.widget.Toolbar = binding.toolbar
//        //setSupportActionBar(toolbar)
//        //setTitle(R.string.settings_name)
//        toolbar.setTitleTextAppearance(requireContext(), R.style.ToolbarStyle)
//        toolbar.setNavigationIcon(R.drawable.toolbar_arrowback)
//        //toolbar.setNavigationOnClickListener { finish() }

//        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        settingsViewModel.intentLiveData.observe(viewLifecycleOwner) {
            openActivity(it)
        }

        settingsViewModel.darkThemeLive.observe(viewLifecycleOwner) {
            binding.themeSwitcher.isChecked = it
        }

        (requireContext().applicationContext as App).checkTheme()
        settingsViewModel.checkDarkTheme((requireContext().applicationContext as App).darkTheme)


        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (requireActivity().applicationContext as App).switchTheme(isChecked)
            settingsViewModel.toggleTheme(isChecked)
        }


        binding.share.setOnClickListener {
            settingsViewModel.shareApp(
                requireContext().getString(R.string.course_url)
            )
        }

        binding.support.setOnClickListener {
            settingsViewModel.getHelp(
                EmailData(
                    email = requireContext().getString(R.string.my_email),
                    subject = requireContext().getString(R.string.email_subject),
                    text = requireContext().getString(R.string.email_text),
                )
            )
        }

        binding.userAgreement.setOnClickListener {
            settingsViewModel.userAgreement(
                requireContext().getString(R.string.practicum_offer)
            )
        }

    }

    private fun generateIntent(intentType: IntentType): Intent {
        when (intentType) {
            is IntentType.ShareApp -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, intentType.link)
                intent.setType("text/plain")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }

            is IntentType.GetHelp -> {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse(MAIL_TO)
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(intentType.emailData.email))
                intent.putExtra(Intent.EXTRA_SUBJECT, intentType.emailData.subject)
                intent.putExtra(Intent.EXTRA_TEXT, intentType.emailData.text)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }

            is IntentType.UserAgreement -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(intentType.link)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                return intent
            }
        }
    }

    private fun openActivity(receivedIntent: IntentType) {
        if (!receivedIntent.isLaunched) {
            startActivity(generateIntent(receivedIntent))
            settingsViewModel.changeIntentStatus()
        }
    }


    companion object {
        const val MAIL_TO = "mailto:"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}