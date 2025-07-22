package com.example.zdravlje.ui.login.parameters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentWaistCircumferenceBinding

class WaistCircumferenceFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentWaistCircumferenceBinding
    interface OnWaistCircumferenceSelectedListener {
        fun onWaistCircumferenceSelected(value: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWaistCircumferenceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val minWaist = 40
        val maxWaist = 110
        val waistValues = Array(maxWaist - minWaist + 1) { i -> "${i + minWaist} cm" }
        binding.pckWaist.minValue = 0
        binding.pckWaist.maxValue = waistValues.size - 1
        binding.pckWaist.displayedValues = waistValues
        binding.pckWaist.wrapSelectorWheel = false
        val defaultHeight = 50
        binding.pckWaist.value = defaultHeight - minWaist

        binding.mbChoose.setOnClickListener {
            val selectedValue = waistValues[binding.pckWaist.value]
            val listener = targetFragment as? OnWaistCircumferenceSelectedListener
            listener?.onWaistCircumferenceSelected(selectedValue)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}
