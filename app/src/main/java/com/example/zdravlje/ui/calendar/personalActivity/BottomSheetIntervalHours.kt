package com.example.zdravlje.ui.calendar.personalActivity

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetIntervalHoursBinding

class BottomSheetIntervalHours : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetIntervalHoursBinding

    interface OnBottomSheetTimeToListener {
        fun onBottomSheetTimeToSelected(value: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetIntervalHoursBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val timesArr = Array(12) { (it + 1).toString() }

        binding.pckTime.minValue = 0
        binding.pckTime.maxValue = timesArr.size - 1
        binding.pckTime.displayedValues = timesArr
        binding.pckTime.value = 0

        binding.mbChoose.setOnClickListener {
            val selectedValue = timesArr[binding.pckTime.value]
            val listener = targetFragment as? OnBottomSheetTimeToListener
            listener?.onBottomSheetTimeToSelected(selectedValue)
            dismiss()
        }

        binding.mbClose.setOnClickListener(){
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}
