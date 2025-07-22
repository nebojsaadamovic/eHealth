package com.example.zdravlje.ui.healthcare.appointment_scheduling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.EventHistoryDataRequest
import com.example.zdravlje.data.model.response.healthcare.EventHistoryDataResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.AppointmentSchedulingHistoryAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch

class AppointmentHistoryFragment : BaseFragment(), PinBottomSheetFragment.OnPinListener {

    private lateinit var binding: FragmentSecondBinding
    private lateinit var appointmentSchedulingHistoryAdapter: AppointmentSchedulingHistoryAdapter
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideShimmer(true)
        setViewModels()
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        healthcareViewModel.getPastScheduledLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null) {
                    if (response.isSuccessful) {
                        showHideShimmer(false)
                        response.body()?.let { data ->
                            if (data.isNullOrEmpty()) {
                                binding.clWrapperData.visibility = View.VISIBLE
                                binding.rvAppointment.visibility = View.GONE
                            } else {
                                setAdapterHistory(data)
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
                }
            })
    }

    private fun setAdapterHistory(dataList: List<EventHistoryDataResponse>) {
        appointmentSchedulingHistoryAdapter =
            AppointmentSchedulingHistoryAdapter(dataList, requireActivity().supportFragmentManager)
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.rvAppointment.layoutManager = linearLayoutManager
        binding.rvAppointment.adapter = appointmentSchedulingHistoryAdapter
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

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    override fun onPinChange() {
        healthcareViewModel.isPinEntered = true
        val currentTime = DateUtils.getCurrentTimeyyyyMMddHHmm()
        healthcareViewModel.getPastScheduledAppointments(
            EventHistoryDataRequest(
                eventTypeIds = listOf(2),
                startTimeFrom = "1998-01-01 00:00",
                startTimeTo = currentTime,
                limit = 10,
                page = 1,
                pin = getStoredPin(),
                sort = "DESC"
            )
        )
    }

    override fun onPinDismiss() {
        showHideShimmer(false)
        binding.clWrapperData.visibility = View.VISIBLE
        binding.rvAppointment.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            if (isPinSet()) {
                val currentTime = DateUtils.getCurrentTimeyyyyMMddHHmm()
                healthcareViewModel.getPastScheduledAppointments(
                    EventHistoryDataRequest(
                        eventTypeIds = listOf(2),
                        startTimeFrom = "1998-01-01 00:00",
                        startTimeTo = currentTime,
                        limit = 10,
                        page = 1,
                        pin = getStoredPin(),
                        sort = "DESC"
                    )
                )
            } else {
                showHideShimmer(false)
                binding.clWrapperData.visibility = View.VISIBLE
                binding.rvAppointment.visibility = View.GONE
            }
        }
    }
}