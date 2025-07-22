package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetDosagePeriodBinding


class BottomSheetDosageFragment(
    private var atribute: String,
    private val fieldType: FieldType
) : BottomSheetDialogFragment() {

    enum class FieldType {
        DOSAGE,
        EVERY_HOUR
    }

    private lateinit var binding: FragmentBottomSheetDosagePeriodBinding
    private var listener: OnValueListener? = null

    interface OnValueListener {
        fun onValueChange(value: String, fieldType: FieldType)
    }

    fun setOnValueListener(listener: OnValueListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDosagePeriodBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.tvTitleHeader.text = atribute
        binding.ietValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrEmpty()
                binding.mbConfirm.isEnabled = hasText
                binding.mbConfirm.alpha = if (hasText) 1.0f else 0.2f
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.mbConfirm.setOnClickListener {
            val value = binding.ietValue.text.toString().trim()
            listener?.onValueChange(value, fieldType)
            dismiss()
        }

        binding.mbCancel.setOnClickListener() {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}