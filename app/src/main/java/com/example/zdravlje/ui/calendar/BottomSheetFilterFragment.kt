package com.example.zdravlje.ui.calendar

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetFilterBinding

class BottomSheetFilterFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetFilterBinding
    private var listener: OnFilterListener? = null

    interface OnFilterListener {
        fun onFilterChange(filter: Int)
    }

    fun setOnFilterListener(listener: OnFilterListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbClose.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        binding.mbConfirm.setOnClickListener {
            if (binding.reminderRadioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(
                    context,
                    getString(R.string.fragment_bottom_sheet_filter_must_select_something),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val selectedRadioButtonId = binding.reminderRadioGroup.checkedRadioButtonId
                val selectedValue = when (selectedRadioButtonId) {
                    R.id.reminderDrugRadioButton -> 1
                    R.id.scheduledDrAppointmentsRadioButton -> 2
                    R.id.availableTherapyRadioButton -> 3
                    R.id.personalActivityRadioButton -> 4
                    R.id.screeningExaminationsRadioButton -> 5
                    R.id.rbScheduledCalls -> 7
                    else -> -1
                }
                listener?.onFilterChange(selectedValue)
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}
