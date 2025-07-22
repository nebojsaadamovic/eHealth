package com.example.zdravlje.ui.calendar.nextOnSchedule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetConfirmDeleteReminderBinding

class BottomSheetConfirmDeleteReminderFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetConfirmDeleteReminderBinding
    private var checkedListener: OnCheckedListener? = null

    interface OnCheckedListener {
        fun onChecked(isChecked: Int)
    }

    fun setCheckedListener(listener: OnCheckedListener) {
        checkedListener = listener
    }

    private var textSource: String? = null
    fun setTextSource(source: String) {
        textSource = source
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetConfirmDeleteReminderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        when (textSource) {
            "DeleteReminder-Drug consumption" -> {
                binding.mtvDescription.text =
                    getString(R.string.fragment_bottom_sheet_confirm_delete_reminder_description_drug_consumption)
            }

            "DeleteReminder-Personal data activity" -> {
                binding.mtvDescription.text =
                    getString(R.string.fragment_bottom_sheet_confirm_delete_reminder_description_personal_data_activity)
            }

            "DeleteReminder-Scheduled Call" -> {
                binding.mtvDescription.text =
                    getString(R.string.fragment_bottom_sheet_confirm_delete_reminder_description_scheduled_call)
            }

            "DeleteMedicamente-PrescribedMedicamentOptionsFragment" -> {
                binding.mtvDescription.text =
                    getString(R.string.fragment_bottom_sheet_confirm_delete_reminder_delete_medicamente)
                binding.mvHeaderTitle.text =
                    getString(R.string.fragment_bottom_sheet_confirm_delete_reminder_delete_medicamente_header)
                binding.radioBtn.visibility = View.GONE
                binding.mtvRadioText.visibility = View.GONE
            }

            "DeleteReminder-DoctorsAppointment" -> {
                binding.mtvDescription.text =
                    getString(R.string.fragment_bottom_sheet_confirm_delete_reminder_description_doctors_appointment)
                binding.radioBtn.visibility = View.GONE
                binding.mtvRadioText.visibility = View.GONE
            }

            "SchedulingAppointment" -> {
                binding.mtvDescription.text =
                    getString(R.string.fragment_bottom_sheet_confirm_delete_reminder_description_doctors_appointment)
                binding.radioBtn.visibility = View.GONE
                binding.mtvRadioText.visibility = View.GONE
                binding.mvHeaderTitle.text = getString(R.string.attention)
                binding.mbContinue.text = getString(R.string.yes)
                binding.mbClose.text = getString(R.string.no)
            }
        }

        binding.mbClose.setOnClickListener() {
            dismiss()
        }

        binding.mbContinue.setOnClickListener() {
            val isChecked = if (binding.radioBtn.isChecked) 1 else 0
            checkedListener?.onChecked(isChecked)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}