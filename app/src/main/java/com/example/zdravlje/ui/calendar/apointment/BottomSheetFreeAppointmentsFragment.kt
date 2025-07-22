package com.example.zdravlje.ui.calendar.apointment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.calendar.Timeslot
import com.example.zdravlje.ui.adaptersAndViewHolders.calendar.TimeSlotsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetFreeAppointmentsBinding

class BottomSheetFreeAppointmentsFragment(
    private val timeslot: List<Timeslot>
) : BottomSheetDialogFragment(), TimeSlotsAdapter.OnRadioListener {

    lateinit var binding: FragmentBottomSheetFreeAppointmentsBinding
    private lateinit var timeSlotsAdapter: TimeSlotsAdapter
    var radioValueFrom: String? = null
    var radioValueTo: String? = null
    var timeslotId: String? = null

    interface OnAppointmentListener {
        fun onAppointmentChange(from: String, to: String, timeslotId: String)
    }

    private var listener: OnAppointmentListener? = null
    fun setOnAppointmentListener(listener: OnAppointmentListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetFreeAppointmentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setupUI()
    }

    private fun setAdapter() {
        timeSlotsAdapter = TimeSlotsAdapter(timeslot, this)
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.reciclerViewTerms.layoutManager = linearLayoutManager
        binding.reciclerViewTerms.adapter = timeSlotsAdapter
    }

    private fun setupUI() {
        binding.mbChoose.setOnClickListener {
            if (!radioValueFrom.isNullOrEmpty() && !radioValueTo.isNullOrEmpty()) {
                listener?.onAppointmentChange(
                    radioValueFrom ?: "",
                    radioValueTo ?: "",
                    timeslotId ?: ""
                )

                dismiss()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.fragment_bottomsheet_dialog_confirm_toast_select_date),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.mbClose.setOnClickListener() {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    override fun onRadioChange(from: String, to: String, id: String) {
        radioValueFrom = from
        radioValueTo = to
        timeslotId = id
    }
}