package com.example.zdravlje.ui.calendar.apointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.calendar.DoctorAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentCalendarAppointmentExaminationBinding

class CalendarAppointmentExaminationFragment(
   private val  className: String?=null
) : BaseFragment() {

    lateinit var binding: FragmentCalendarAppointmentExaminationBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var doctorAdapter: DoctorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarAppointmentExaminationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {
        showProgressBar()

        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        doctorAdapter = DoctorAdapter(emptyList()) {}
        binding.rvDoctors.adapter = doctorAdapter

        getFamilyDoctors()

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
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

    private fun getFamilyDoctors() {
        calendarViewModel.getFamilyDoctors(PinRequest(getStoredPin()))
    }

    private fun subscribeToObservers() {
        calendarViewModel.getFamilyDoctorLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                hideProgressBar()
                doctorAdapter = DoctorAdapter(response.body()!!) {
                    if (className == "HealthcareFragment") {
                        CommonUtils.openFragmentHome(
                            requireActivity(),
                            CalendarAppointmentScheduleFragment(it, className)
                        )
                    } else { CommonUtils.openFragmentCalendar(
                            requireActivity(),
                            CalendarAppointmentScheduleFragment(it, className)
                        )
                    }
                }

                binding.rvDoctors.adapter = doctorAdapter

            } else {
                Toast.makeText(requireContext(), "${response.body()}", Toast.LENGTH_SHORT).show()
            }
        }

        calendarViewModel.getFamilyDoctorErrorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            binding.layoutNoData.mtvTitleNoData.text = getString(R.string.fragment_calendar_appointment_eaxamination_patient_not_found_rfzo)
            binding.clWrapperData.visibility= View.VISIBLE
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