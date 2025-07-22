package com.example.zdravlje.ui.login.parameters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBloodGroupBinding

class BloodGroupFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBloodGroupBinding

    interface OnBloodGroupSelectedListener {
        fun onBloodGroupSelected(value: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBloodGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bloodGroups = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        binding.pckBloodGroup.minValue = 0
        binding.pckBloodGroup.maxValue = bloodGroups.size - 1
        binding.pckBloodGroup.displayedValues = bloodGroups
        binding.pckBloodGroup.value = 0

        binding.mbChoose.setOnClickListener {
            val selectedValue = bloodGroups[binding.pckBloodGroup.value]
            val listener = targetFragment as? OnBloodGroupSelectedListener
            listener?.onBloodGroupSelected(selectedValue)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}