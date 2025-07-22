package com.example.zdravlje.ui.healthcare.appointment_scheduling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.calendar.ExamDeleteRequest
import com.example.zdravlje.data.model.response.healthcare.AppointmentResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.AppointmentSchedulingAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetConfirmDeleteReminderFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentFirstBinding

class ScheduledAppointmentsFragment : BaseFragment(),
    BottomSheetConfirmDeleteReminderFragment.OnCheckedListener
{
    private lateinit var binding: FragmentFirstBinding
    private lateinit var appointmentSchedulingAdapter: AppointmentSchedulingAdapter
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var examNum: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideShimmer(true)
        setViewModels()
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        healthcareViewModel.getFutureExams(PinRequest(pin = getStoredPin()))
        healthcareViewModel.getFutureExamsLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null) {
                    if (response.isSuccessful) {
                        showHideShimmer(false)
                        response.body()?.let { data ->
                            if (data.isNullOrEmpty()){
                                binding.clWrapperData.visibility=View.VISIBLE
                                binding.rvAppointment.visibility = View.GONE
                            }else {
                                setAdapter(data)
                                binding.rvAppointment.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        showHideShimmer(false)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else{
                    showHideShimmer(false)
                    binding.clWrapperData.visibility = View.VISIBLE
                }
            })


        healthcareViewModel.deleteExamLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null) {
                    if (response.isSuccessful) {
                        showHideShimmer(false)
                        response.body()?.let { data ->
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.fragment_scheduled_appointments_toast_delete_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                            requireActivity().finish()
                        }
                    } else {
                        showHideShimmer(false)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

        healthcareViewModel.deleteExamErrorLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                Toast.makeText(requireActivity(), "${getString(R.string.error)}: $response", Toast.LENGTH_SHORT).show()
            })
    }

    private fun setAdapter(dataList: List<AppointmentResponse>) {
        appointmentSchedulingAdapter = AppointmentSchedulingAdapter(dataList, parentFragmentManager)
        { deleteAppointmentScheduled ->
            handleItemClick(deleteAppointmentScheduled)
        }
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.rvAppointment.layoutManager = linearLayoutManager
        binding.rvAppointment.adapter = appointmentSchedulingAdapter
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(
            requireActivity(), healthcareViewModelFactory
        ).get(HealthcareViewModel::class.java)
    }

    private fun handleItemClick(item: AppointmentResponse) {
        examNum = item.number

        val bottomSheet = BottomSheetConfirmDeleteReminderFragment()
        bottomSheet.setCheckedListener(this)
        bottomSheet.setTextSource("SchedulingAppointment")
        bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
    }

    override fun onChecked(isChecked: Int) {
        getStoredPin()?.let { pin ->
            healthcareViewModel.deleteExam(
                ExamDeleteRequest(
                    pin = pin,
                    examNumber = examNum
                )
            )
        }
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    fun reloadFutureExams() {
        showHideShimmer(true)
        healthcareViewModel.getFutureExams(PinRequest(pin = getStoredPin()))
    }
}