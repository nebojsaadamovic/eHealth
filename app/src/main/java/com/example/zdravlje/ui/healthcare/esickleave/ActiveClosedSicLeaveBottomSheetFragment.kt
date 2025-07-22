package com.example.zdravlje.ui.healthcare.esickleave

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentActiveClosedSicLeaveBottomSheetBinding

class ActiveClosedSicLeaveBottomSheetFragment(
    private var eSickLeaveArr: Array<SickLeaveResponse>
) : BottomSheetDialogFragment() {
    val closedSicLeave = "Zatvoreno" // ne prevoditi stringove, da se ne pokvari
    val activeSicLeave = "Aktivno" // ne prevoditi stringove, da se ne pokvari

    interface OnValueListener {
        fun onValueChange(value: Array<SickLeaveResponse>)
    }

    private var listener: OnValueListener? = null
    fun setOnValueListener(listener: OnValueListener) {
        this.listener = listener
    }

    lateinit var binding: FragmentActiveClosedSicLeaveBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActiveClosedSicLeaveBottomSheetBinding.inflate(layoutInflater)
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
            val selectedRadioButtonId = binding.statusRadioGroup.checkedRadioButtonId

            val sickManagementStatus: String

            when (selectedRadioButtonId) {
                R.id.rbActive -> {
                    sickManagementStatus = activeSicLeave
                }

                R.id.rbClosed -> {
                    sickManagementStatus = closedSicLeave
                }

                else -> {
                    sickManagementStatus = ""
                }
            }

            val filteredSickLeaveArr = if (sickManagementStatus.isNotEmpty()) {
                eSickLeaveArr.filter { it.sickManagementStatus == sickManagementStatus }.toTypedArray()
            } else {
                eSickLeaveArr
            }

            listener?.onValueChange(filteredSickLeaveArr)

            val itemCount = filteredSickLeaveArr.size
            Toast.makeText(
                requireContext(),
                "${getString(R.string.fragment_active_closed_sic_leave_bottom_sheet_filtered)}: $itemCount " +
                        getString(R.string.fragment_active_closed_sic_leave_bottom_sheet_records),
                Toast.LENGTH_SHORT
            )
                .show()
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}