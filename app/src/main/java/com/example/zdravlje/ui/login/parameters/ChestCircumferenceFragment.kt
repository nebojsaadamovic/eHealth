package com.example.zdravlje.ui.login.parameters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChestCircumferenceBinding

class ChestCircumferenceFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentChestCircumferenceBinding

    interface OnChestCircumferenceListener {
        fun onChestCircumferenceSelected(value: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChestCircumferenceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val minHeight = 25
        val maxHeight = 60
        val chestValues = Array(maxHeight - minHeight + 1) { i -> "${i + minHeight} cm" }
        binding.pckChestCircumference.minValue = 0
        binding.pckChestCircumference.maxValue = chestValues.size - 1
        binding.pckChestCircumference.displayedValues = chestValues
        binding.pckChestCircumference.wrapSelectorWheel = false
        val defaultHeight = 30
        binding.pckChestCircumference.value = defaultHeight - minHeight

        binding.mbChoose.setOnClickListener {
            val selectedValue = chestValues[binding.pckChestCircumference.value]
            val listener = targetFragment as? OnChestCircumferenceListener
            listener?.onChestCircumferenceSelected(selectedValue)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}