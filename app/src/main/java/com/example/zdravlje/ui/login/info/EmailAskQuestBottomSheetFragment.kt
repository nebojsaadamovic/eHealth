package com.example.zdravlje.ui.login.info

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.zdravlje.utilities.ValidationUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentEmailAskQuestBottomSheetBinding

class EmailAskQuestBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentEmailAskQuestBottomSheetBinding
    private var emailListener: OnEmailListener? = null

    interface OnEmailListener {
        fun onEmailChange(email: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailAskQuestBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableBtnConfirm()
        setupUI()
    }

    private fun setupUI() {
        binding.mbConfirm.setOnClickListener(View.OnClickListener {
            val email = binding.tietEmail.text.toString().trim()
            emailListener?.onEmailChange(email)
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_email_ask_quest_bottom_sheet_successful_email_entry),
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        })

        binding.mbCancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    private fun enableBtnConfirm() {
        binding.tietEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.tietEmail.text.toString().trim()
                if (ValidationUtils.isEmailValid(email)) {
                    binding.mbConfirm.isEnabled = true
                    binding.mbConfirm.alpha = 1.0f
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    fun setOnEmailListener(listener: OnEmailListener) {
        emailListener = listener
    }
}