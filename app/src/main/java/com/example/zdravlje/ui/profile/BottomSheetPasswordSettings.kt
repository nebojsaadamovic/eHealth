package com.example.zdravlje.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetPasswordSettingsBinding
import java.text.Normalizer

class BottomSheetPasswordSettings : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetPasswordSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetPasswordSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mtvCloseBtn.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        binding.mtvChangePass.setOnClickListener {
            binding.etNewPassword.visibility = View.VISIBLE
            binding.etNewPassword.requestFocus()
        }

        binding.mtvConfrimPassBtn.setOnClickListener {
            binding.etConfirmPassword.visibility = View.VISIBLE
            binding.etConfirmPassword.requestFocus()
        }

        binding.mtvSave.setOnClickListener {
            val newPassword = sanitizeInput(binding.etNewPassword.text.toString().trim())
            val confirmPassword = sanitizeInput(binding.etConfirmPassword.text.toString().trim())
            if (newPassword.equals(confirmPassword)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_bottom_sheet_password_settings_data_saved_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
                val userInfoFragment = UserInfoFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_container_view, userInfoFragment)
                    .addToBackStack(null).commit()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_bottom_sheet_password_settings_passwords_do_not_match),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sanitizeInput(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFC)
            .replace(Regex("[^\\p{L}\\p{N}]+"), "")
            .trim()
            .replace("‘", "'")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }
}
