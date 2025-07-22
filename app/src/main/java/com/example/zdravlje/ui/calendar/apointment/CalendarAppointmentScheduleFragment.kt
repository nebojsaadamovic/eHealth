package com.example.zdravlje.ui.calendar.apointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.calendar.FamilyTimeSlots
import com.example.zdravlje.data.model.request.calendar.FamilyTimeSlotsForTelemedicine
import com.example.zdravlje.data.model.response.calendar.FamilyDoctor
import com.example.zdravlje.data.model.response.calendar.ScheduleResponse
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentCalendarAppointmentScheduleBinding

class CalendarAppointmentScheduleFragment(
    private var familyDoctor: FamilyDoctor,
    private val  className: String?=null
) : BaseFragment() {

    lateinit var binding: FragmentCalendarAppointmentScheduleBinding
    private var selectedType: Int = 0

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var daysTimeslots: List<ScheduleResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarAppointmentScheduleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.mtvDoctorName.text = familyDoctor.name
        binding.mtvDoctorInstitution.text = familyDoctor.institution
        binding.clAmbulance.setOnClickListener { onTypeSelected(it) }
        binding.clTelemedicineAudio.setOnClickListener { onTypeSelected(it) }
        binding.clTelemedicineVideo.setOnClickListener { onTypeSelected(it) }
        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    fun onTypeSelected(view: View) {
        when (view.id) {
            R.id.clAmbulance -> {
                selectedType = 1
                binding.mtvTypeValue.text =
                    getString(R.string.fragment_calendar_appointment_schedule_appointment_type_ambulatory)
                showProgressBar()
                getFamilyTimeslots()
            }

            R.id.clTelemedicineAudio -> {
                selectedType = 2
                binding.mtvTypeValue.text =
                    getString(R.string.fragment_calendar_appointment_schedule_appointment_type_telemedicine_audio)
                showProgressBar()
                getFamilyTimeslotsForTelemedicine()
            }

            R.id.clTelemedicineVideo -> {
                selectedType = 3
                binding.mtvTypeValue.text =
                    getString(R.string.fragment_calendar_appointment_schedule_appointment_type_telemedicine_video)
                showProgressBar()
                getFamilyTimeslotsForTelemedicine()
            }
        }
    }

    private fun getFamilyTimeslots() {
        calendarViewModel.getFamilyTimeSlots(FamilyTimeSlots(familyDoctor.doctor_id, getStoredPin()))
    }

    private fun getFamilyTimeslotsForTelemedicine() {
        calendarViewModel.getFamilyTimeSlotsForTelemedicine(FamilyTimeSlotsForTelemedicine(familyDoctor.doctor_id, getStoredPin()))
    }

    fun showNewElements() {
        binding.clAmbulance.visibility = View.GONE
        binding.clTelemedicineAudio.visibility = View.GONE
        binding.clTelemedicineVideo.visibility = View.GONE
        binding.clTelemedicineVideo.visibility = View.GONE
        binding.tvAddMedicament.text =
            getString(R.string.fragment_calendar_appointment_schedule_choose_date)

        binding.clDate.visibility = View.VISIBLE
        binding.llType.visibility = View.VISIBLE
    }

    private fun setViewModels() {
        val calendarRepository = CalendarRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val calendarViewModelFactory =
            CalendarViewModelFactory(calendarRepository, dataStoreRepository)

        calendarViewModel = ViewModelProvider(
            requireActivity(),
            calendarViewModelFactory
        ).get(CalendarViewModel::class.java)
    }

    private fun subscribeToObservers() {
        calendarViewModel.getFamilyTimeSlotsLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                response.body()?.let { scheduleList ->
                    daysTimeslots = scheduleList.map { it }
                }
                val ambulantExaminationFragment = AmbulantExaminationFragment(
                    familyDoctor.name
                        ?: getString(R.string.fragment_calendar_appointment_schedule_unknown_name),
                    familyDoctor.institution
                        ?: getString(R.string.fragment_calendar_appointment_schedule_unknown_institution),
                    (binding.mtvTypeValue.text
                        ?: getString(R.string.fragment_calendar_appointment_schedule_unknown_type)).toString(),
                    daysTimeslots,
                    className = className
                )

                if (className == "HealthcareFragment") {
                    CommonUtils.openFragmentHome(requireActivity(), ambulantExaminationFragment)
                } else {
                    CommonUtils.openFragmentCalendar(requireActivity(), ambulantExaminationFragment)
                }

                hideProgressBar()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_calendar_appointment_schedule_unauthorized_request),
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressBar()
            }
        }

        calendarViewModel.getFamilyTimeSlotsErrorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireActivity(),
                getString(R.string.fragment_calendar_appointment_schedule_unauthorized_request),
                Toast.LENGTH_SHORT
            ).show()
            hideProgressBar()
        }

        calendarViewModel.getFamilyTimeSlotsForTelemedicineLiveData.observe(viewLifecycleOwner) { response ->
            if (response.body()!!.size > 0) {
                showNewElements()
                hideProgressBar()

            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_calendar_appointment_schedule_no_available_appointments),
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressBar()
            }
        }

        calendarViewModel.getFamilyTimeSlotsErrorForTelemedicineLiveData.observe(viewLifecycleOwner) {response ->
            Toast.makeText(requireActivity(), "${getString(R.string.error)}: $response", Toast.LENGTH_SHORT).show()
            hideProgressBar()
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}