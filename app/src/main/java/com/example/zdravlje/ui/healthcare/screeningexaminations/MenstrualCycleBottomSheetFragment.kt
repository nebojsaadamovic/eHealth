package com.example.zdravlje.ui.healthcare.screeningexaminations

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMenstrualCycleBottomSheetBinding

class MenstrualCycleBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentMenstrualCycleBottomSheetBinding
    private var listener: OnValueListener? = null
    private var fieldTag: String? = null

    interface OnValueListener {
        fun OnValueChange(value: String)
    }

    fun setOnValueListener(listener: OnValueListener, fieldTag: String) {
        this.listener = listener
        this.fieldTag = fieldTag
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenstrualCycleBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbClose.setOnClickListener() {
            dismiss()
        }
        binding.mbConfirm.setOnClickListener {
            val selectedRadioButtonId = binding.rgMenstrualCyclus.checkedRadioButtonId
            val value: String
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton =
                    binding.rgMenstrualCyclus.findViewById<RadioButton>(selectedRadioButtonId)
                value = selectedRadioButton.text.toString()
                listener?.OnValueChange(value)
            }
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}