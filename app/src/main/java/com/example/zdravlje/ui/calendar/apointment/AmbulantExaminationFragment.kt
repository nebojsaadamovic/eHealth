package com.example.zdravlje.ui.calendar.apointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.calendar.ScheduleResponse
import com.example.zdravlje.utilities.CommonUtils
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAmbulantExaminationBinding
import java.util.Calendar

class AmbulantExaminationFragment(
    private val familyDoctor: String,
    private val institution: String,
    private val selectedType: String,
    private val daysTimeslots: List<ScheduleResponse>,
    private val  className: String?=null
) : Fragment(), BottomSheetFreeAppointmentsFragment.OnAppointmentListener,
    BottomSheetCalendarFragment.DateSelectedListener {

    lateinit var binding: FragmentAmbulantExaminationBinding
    private var formattedSelectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAmbulantExaminationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mtvChooseDate.setOnClickListener {
            val bottomSheetFragment = BottomSheetCalendarFragment()
            bottomSheetFragment.setDateSelectedListener(this)

            val availableDates = getAvailableDates()

            if (availableDates.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_ambulant_examination_toast_no_available_appointments),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                bottomSheetFragment.setAvailableDates(availableDates)
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }
        }

        binding.mtvDoctorValue.text = familyDoctor
        binding.mtvInstitutionValue.text = institution
        binding.mtvTypeExaminationValue.text = selectedType

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDateSelected(date: String) {
        formattedSelectedDate = date
        binding.mtvChooseDate.text = formattedSelectedDate
        findFreeTermsForDatum(formattedSelectedDate ?: "")
    }

    private fun getAvailableDates(): List<CalendarDay> {
        val availableDates = mutableListOf<CalendarDay>()
        val today = Calendar.getInstance()

        daysTimeslots.forEach { schedule ->
            if (schedule.timeslots.timeslot.isNotEmpty()) {
                val dateParts = schedule.day.split(".")
                val day = dateParts[0].toInt()
                val month = dateParts[1].toInt()
                val year = dateParts[2].toInt()
                availableDates.add(CalendarDay.from(year, month, day))
            }
        }

        if (availableDates.size >= 2) {
            val first = availableDates[0]
            val isToday = first.year == today.get(Calendar.YEAR) &&
                    first.month == today.get(Calendar.MONTH) + 1 &&
                    first.day == today.get(Calendar.DAY_OF_MONTH)

            if (isToday) {
                return listOf(availableDates[1]) // vrati drugi ako je prvi danas
            }
        }

        return availableDates
    }


    private fun findFreeTermsForDatum(datePick: String) {
        val selectedSchedule = daysTimeslots.find { it.day == datePick }

        if (selectedSchedule != null) {
            val timeslots = selectedSchedule.timeslots.timeslot
            val bottomSheet = BottomSheetFreeAppointmentsFragment(timeslots)
            bottomSheet.setOnAppointmentListener(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        } else {
            Toast.makeText(
                requireActivity(),
                " ${getString(R.string.fragment_ambulant_examination_toast_no_available_appointments_for_date)} $datePick",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onAppointmentChange(from: String, to: String, timeslotId: String) {
        if (from.isNotEmpty() && to.isNotEmpty()) {
            val reviewEnteredDataFragment = ReviewEnteredDataFragment(
                from,
                to,
                formattedSelectedDate ?: "",
                familyDoctor,
                institution,
                selectedType,
                timeslotId,
                className = className
            )

            if (className == "HealthcareFragment") {
                CommonUtils.openFragmentHome(requireActivity(), reviewEnteredDataFragment)
            } else {
                CommonUtils.openFragmentCalendar(requireActivity(), reviewEnteredDataFragment)
            }

        } else {
            Toast.makeText(
                context,
                getString(R.string.fragment_ambulant_examination_toast_invalid_appointment_times),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}