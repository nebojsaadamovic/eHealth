package com.example.zdravlje.ui.login.parameters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentWeightBinding

class WeightFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentWeightBinding

    interface OnWeightListener {
        fun onWeightSelected(value: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeightBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val minWeight = 45
        val maxWeight = 200
        val weightValues = Array(maxWeight - minWeight + 1) { i ->
            "${i + minWeight} kg"
        }
        binding.pckWeight.minValue = 0
        binding.pckWeight.maxValue = weightValues.size - 1
        binding.pckWeight.displayedValues = weightValues
        binding.pckWeight.wrapSelectorWheel = false
        val defaultHeight = 72
        binding.pckWeight.value = defaultHeight - minWeight

        binding.mbChoose.setOnClickListener {
            val selectedValue = weightValues[binding.pckWeight.value]
            val listener = targetFragment as? OnWeightListener
            listener?.onWeightSelected(selectedValue)
            dismiss()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

}