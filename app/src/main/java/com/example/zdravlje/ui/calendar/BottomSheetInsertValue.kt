package com.example.zdravlje.ui.calendar

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetInsertValueBinding

class BottomSheetInsertValue : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetInsertValueBinding
    private var fieldTag: String? = null
    private var listener: OnValueListener? = null

    interface OnValueListener {
        fun onValueChange(value: String)
    }

    fun setOnValueListener(listener: OnValueListener, fieldTag: String?=null) {
        this.listener = listener
        this.fieldTag = fieldTag
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetInsertValueBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString("title") ?: "Default Title"
        val subtitle = arguments?.getString("subtitle") ?: "Default Subtitle"
        binding.tvTitleHeader.text = title
        if(!subtitle.isNullOrEmpty()){
            binding.tilValue.hint = subtitle
        }
        setupUI()
    }

    private fun setupUI() {
        binding.ietValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrEmpty()
                binding.mbConfirm.isEnabled = hasText
                binding.mbConfirm.alpha = if (hasText) 1.0f else 0.2f
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.mbConfirm.setOnClickListener(View.OnClickListener {
            val value = binding.ietValue.text.toString().trim()
            listener?.onValueChange(value, )
            dismiss()
        })

        binding.mbCancel.setOnClickListener() {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}