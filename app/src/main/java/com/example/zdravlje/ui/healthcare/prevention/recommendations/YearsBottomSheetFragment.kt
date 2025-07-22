package com.example.zdravlje.ui.healthcare.prevention.recommendations

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentYearsBottomSheetBinding

class YearsBottomSheetFragment(
): BottomSheetDialogFragment() {

    lateinit var binding: FragmentYearsBottomSheetBinding
    interface OnYearSelectedListener {
        fun onYearSelected(value: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYearsBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val minYear = 2022
        val maxYear = 2025
        val yearsValues = Array(maxYear - minYear + 1) { i -> "${minYear + i}" }

        binding.yearPicker.minValue = 0
        binding.yearPicker.maxValue = yearsValues.size - 1
        binding.yearPicker.displayedValues = yearsValues
        binding.yearPicker.wrapSelectorWheel = false

        val defaultHeight = 2025
        binding.yearPicker.value = defaultHeight - minYear

        binding.mbChoose.setOnClickListener {
            val selectedValue = yearsValues[binding.yearPicker.value]
            val listener = targetFragment as? OnYearSelectedListener
            listener?.onYearSelected(selectedValue)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}