package com.example.zdravlje.ui.profile.settings

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zdravlje.utilities.ValidationUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentEmailBottomSheetBinding

class EmailFragmentBottomSheet : BottomSheetDialogFragment() {

    interface OnEmailListener {
        fun onEmailChange(email: String)
    }

    private var listener: OnEmailListener? = null

    fun setOnPhoneNumListener(listener: OnEmailListener) {
        this.listener = listener
    }

    lateinit var binding: FragmentEmailBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        enableBtnConfirm()
    }

    private fun setupUI() {
        binding.mbConfirm.setOnClickListener(View.OnClickListener {
            val email = binding.ietEmail.text.toString().trim()
            listener?.onEmailChange(email)
            dismiss()
        })

        binding.mbCancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    private fun enableBtnConfirm() {
        binding.ietEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.ietEmail.text.toString().trim()
                if (ValidationUtils.isEmailValid(email)) {
                    binding.mbConfirm.isEnabled = true
                    binding.mbConfirm.alpha = 1.0f
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}