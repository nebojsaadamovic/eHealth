package com.example.zdravlje.ui.login.parameters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentHeightBinding

class HeightFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentHeightBinding
    interface OnHeightSelectedListener {
        fun onHeightSelected(value: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHeightBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val minHeight = 150
        val maxHeight = 215
        val heightValues = Array(maxHeight - minHeight + 1) { i -> "${i + minHeight} cm" }
        binding.heightPicker.minValue = 0
        binding.heightPicker.maxValue = heightValues.size - 1
        binding.heightPicker.displayedValues = heightValues
        binding.heightPicker.wrapSelectorWheel = false
        val defaultHeight = 186
        binding.heightPicker.value = defaultHeight - minHeight

        binding.mbChoose.setOnClickListener {
            val selectedValue = heightValues[binding.heightPicker.value]
            val listener = targetFragment as? OnHeightSelectedListener
            listener?.onHeightSelected(selectedValue)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}