package com.example.zdravlje.ui.home.dashboard.event_details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.calendar.DeleteEvent
import com.example.zdravlje.data.model.response.dashboard.event_payload.AppointmentEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetConfirmDeleteReminderFragment
import com.example.zdravlje.ui.home.HomeActivity
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.databinding.FragmentDoctorsAppointmentBinding

class DoctorsAppointmentFragment : BaseFragment(),
    BottomSheetConfirmDeleteReminderFragment.OnCheckedListener {

    private lateinit var binding: FragmentDoctorsAppointmentBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var appointmentEventPayload: AppointmentEventPayload? = null
    private var event: Event? = null
    private var className: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoctorsAppointmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            appointmentEventPayload = it.getParcelable("doctors_appointment_details")
            event = it.getSerializable("event") as? Event
            className = it.getString("class_name").toString()
        }

        if (className == "NextOnScheduleCalendarFragment") {
            if (event!!.isFuture()) {
                binding.ivDelete.visibility = View.VISIBLE

            }
        }

        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {
        appointmentEventPayload?.let { payload ->
            try {
                binding.mtvDate.text = payload.date?.let { DateUtils.getDateWithWeekName(it) }
            } catch (e: Exception) {
                binding.mtvDate.text = payload.date
            }

            binding.mtvDoctorValue.text = payload.doctorName
            binding.mtvInstitutionValue.text = payload.institutionName
            binding.mtvTimeValue.text = payload.time
        }

        binding.ivDelete.setOnClickListener() {
            val bottomSheet = BottomSheetConfirmDeleteReminderFragment()
            bottomSheet.setCheckedListener(this)
            bottomSheet.setTextSource("DeleteReminder-DoctorsAppointment")
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun subscribeToObservers() {
        calendarViewModel.getDeleteEventLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.data_deleted_successfully),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(requireActivity(), HomeActivity::class.java)
                intent.putExtra("open_calendar", true)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun setViewModels() {
        val calendarRepository = CalendarRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val calendarViewModelFactory = CalendarViewModelFactory(
            calendarRepository, dataStoreRepository
        )
        calendarViewModel = ViewModelProvider(
            this, calendarViewModelFactory
        ).get(CalendarViewModel::class.java)
    }

    override fun onChecked(isChecked: Int) {
        if (isPinSet()) {
            val deleteEvent = DeleteEvent(event?.eventId, isChecked, getStoredPin())
            calendarViewModel.deleteEvent(deleteEvent)
        }
    }
}