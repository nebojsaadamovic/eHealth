package com.example.zdravlje.ui.calendar.medicament

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.ui.calendar.CalendarFragment
import com.example.zdravlje.ui.home.HomeActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetConfirmAddMedicamentCalendarBinding

class BottomSheetConfirmAddMedicamentCalendarFragment(
    private var eventObject: Event? = null,
): BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetConfirmAddMedicamentCalendarBinding
    private var confirmListener: OnConfirmListener? = null

    interface OnConfirmListener {
        fun onConfirm()
    }

    fun setConfirmListener(listener: OnConfirmListener) {
        confirmListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetConfirmAddMedicamentCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbConfirm.setOnClickListener {
            confirmListener?.onConfirm()

            val calendarFragment = CalendarFragment()

            if (eventObject != null) {
                requireActivity().finish()

                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra("open_calendar", true)
                startActivity(intent)
                dismiss()
            } else {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.calendar_options_container_view, calendarFragment)
                    .addToBackStack(null).commit()
                dismiss()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_bottom_sheet_confirm_add_medicament_calendar_add_medicine_toast_msg),
                    Toast.LENGTH_SHORT
                )
                    .show()
                requireActivity().finish()
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}