package com.example.zdravlje.ui.profile.settings

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetChooseLanguageBinding

class BottomSheetChooseLanguage : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetChooseLanguageBinding
    private var listener: OnLanguageSelectedListener? = null

    interface OnLanguageSelectedListener {
        fun onLanguageSelected(languageCode: String, languageTag: Int)
    }

    fun setOnLanguageListener(listener: OnLanguageSelectedListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetChooseLanguageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setInitialLanguageSelection()
    }

    private fun setInitialLanguageSelection() {
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedLanguageCode = sharedPref.getString("selected_language", "sr") ?: "sr"

        when (selectedLanguageCode) {
            "sr-rRS" -> binding.srpskiLatinicaRadioButton.isChecked = true
            "sr" -> binding.srpskiCirilicaRadioButton.isChecked = true
            "en" -> binding.engleskiRadioButton.isChecked = true
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    private fun setupUI() {
        binding.mbClose.setOnClickListener {
            dismiss()
        }

        binding.mbConfirm.setOnClickListener {
            val selectedRadioButtonId = binding.languageRadioGroup.checkedRadioButtonId
            var selectedLanguageCode = ""
            var selectedLanguageTag: Int? = null

            when (selectedRadioButtonId) {
                R.id.srpskiLatinicaRadioButton -> {
                    selectedLanguageCode = "sr-rRS"
                    selectedLanguageTag = binding.srpskiLatinicaRadioButton.tag.toString().toInt()
                }

                R.id.srpskiCirilicaRadioButton -> {
                    selectedLanguageCode = "sr"
                    selectedLanguageTag = binding.srpskiCirilicaRadioButton.tag.toString().toInt()
                }

                R.id.engleskiRadioButton -> {
                    selectedLanguageCode = "en"
                    selectedLanguageTag = binding.engleskiRadioButton.tag.toString().toInt()
                }

                else -> {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.fragment_bottom_sheet_choose_language_toast_choose_leng),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            storeLanguage(selectedLanguageCode)
            listener?.onLanguageSelected(selectedLanguageCode, selectedLanguageTag)
            dismiss()
        }
    }

    private fun storeLanguage(languageCode: String) {
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selected_language", languageCode)
            apply()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as OnLanguageSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnLanguageSelectedListener")
        }
    }
}