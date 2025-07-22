package com.example.zdravlje.ui.healthcare.esickleave

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.ESickRequest
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.ESickLeaveAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.example.zdravlje.ui.notification.NotificationViewModel
import com.example.zdravlje.ui.notification.NotificationViewModelFactory
import com.example.zdravlje.utilities.DateUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentESickLeaveBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ESickLeaveFragment(
) : BaseFragment(), ActiveClosedSicLeaveBottomSheetFragment.OnValueListener,
    PinBottomSheetFragment.OnPinListener {

    lateinit var binding: FragmentESickLeaveBinding
    lateinit var eSickLeaveAdapter: ESickLeaveAdapter
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private var eSickLeaveArr: Array<SickLeaveResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentESickLeaveBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        showHideShimmer(true)
        fetchESickLeaveData()
        setupUI()
        notificationCount()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.ivFilter.setOnClickListener() {
            val activeClosedSicLeaveBottomSheetFragment =
                eSickLeaveArr?.let { it1 -> ActiveClosedSicLeaveBottomSheetFragment(it1) }
            activeClosedSicLeaveBottomSheetFragment?.setOnValueListener(this)
            activeClosedSicLeaveBottomSheetFragment?.show(
                parentFragmentManager,
                activeClosedSicLeaveBottomSheetFragment.tag
            )
        }

        binding.ivCalendar.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.tvDateFromValue
            ) { selectedDateString ->
                try {
                    val sickLeaveDateFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
                    val datePickerFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                    val selectedDate = datePickerFormat.parse(selectedDateString)

                    if (selectedDate != null) {
                        val filteredList = eSickLeaveArr?.filter {
                            val itemDate = try {
                                sickLeaveDateFormat.parse(it.startDate)
                            } catch (e: Exception) {
                                null
                            }
                            itemDate != null && !itemDate.before(selectedDate)
                        }?.toTypedArray()

                        if (!filteredList.isNullOrEmpty()) {
                            binding.clWrapperData.visibility = View.GONE
                            eSickLeaveAdapter.updateData(filteredList)
                        } else {
                            binding.clWrapperData.visibility = View.VISIBLE
                        }

                        Toast.makeText(
                            requireContext(),
                            "${getString(R.string.fragment_e_sick_leave_filtered_from)}: ${selectedDateString}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.fragment_e_sick_leave_error_selecting_date),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fragment_e_sick_leave_error_processing_date),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setAdapter(data: List<SickLeaveResponse>) {
        eSickLeaveAdapter = ESickLeaveAdapter(
            data.toTypedArray(),
            parentFragmentManager,
            viewLifecycleOwner,
            requireContext(),
            getStoredPin()
        )
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.reciclerViewESickLeave.layoutManager = linearLayoutManager
        binding.reciclerViewESickLeave.adapter = eSickLeaveAdapter
    }

    override fun onValueChange(value: Array<SickLeaveResponse>) {
        if (value.isEmpty()) {
            binding.clWrapperData.visibility = View.VISIBLE
        } else {
            binding.clWrapperData.visibility = View.GONE
            eSickLeaveAdapter.updateData(value)
        }
    }

    private fun notificationCount() {
        notificationViewModel.notificationCount(
            NotificationReadRequest(
                pin = getStoredPin(),
                currentTime = DateUtils.getCurrentTimeyyyyMMddHHmm(),
                unread = 0
            )
        )
        notificationViewModel.notificationCountLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null) {
                    if (response.isSuccessful) {
                        response.body()?.let { responseData ->
                            val total = responseData.total
                            if (total != null && total != 0) {
                                binding.mtvNotificationCount.text = total.toString()
                            } else {
                                binding.ivNotification.setImageResource(R.drawable.iv_bell)
                            }
                        }
                    }
                }
            }
        )
    }

    private fun setUpViewModels() {
        dataStoreRepository = DataStoreRepository(requireActivity())
        val notificationRepository = NotificationRepository()
        val notificationViewModelFactory =
            NotificationViewModelFactory(notificationRepository, dataStoreRepository)
        notificationViewModel = ViewModelProvider(requireActivity(), notificationViewModelFactory)
            .get(NotificationViewModel::class.java)

        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory)
            .get(HealthcareViewModel::class.java)
    }

    private fun fetchESickLeaveData() {
        if (isPinSet()) {
            healthcareViewModel.getPatientSickLeaves(ESickRequest(pin = getStoredPin()))
        } else {
            showHideShimmer(false)
            binding.wrapper.visibility = View.VISIBLE
            binding.reciclerViewESickLeave.visibility = View.GONE

            val mbpinCode =
                binding.wrapper.findViewById<MaterialButton>(R.id.mbEnterPinCode)
            mbpinCode.setOnClickListener {
                val pinBottomSheetFragment = PinBottomSheetFragment()
                pinBottomSheetFragment.setOnPinListener(this)
                pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
            }

            val mtvNetwork = binding.wrapper.findViewById<MaterialTextView>(R.id.mtvNetwork)
            mtvNetwork.text = getString(R.string.fragment_healthcare_e_sick_leave_title)

            val imageMsg = binding.wrapper.findViewById<ImageView>(R.id.ivMsg)
            imageMsg.setImageResource(R.drawable.ic_sick_leave_wrapper)

            val mbLogInButton = binding.wrapper.findViewById<MaterialButton>(R.id.mbLogIn)
            mbLogInButton.setOnClickListener(View.OnClickListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            })

            val ivBackButton = binding.wrapper.findViewById<ImageView>(R.id.ivBack)
            ivBackButton.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        healthcareViewModel.getPatientSickLeavesLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    showHideShimmer(false)
                    response.body()?.let { data ->
                        if (data.isNotEmpty()) {
                            eSickLeaveArr = data.toTypedArray()
                            setAdapter(data)
                        } else {
                            binding.clWrapperData.visibility = View.VISIBLE
                            binding.reciclerViewESickLeave.visibility = View.GONE
                        }
                    }
                } else {
                    showToast("${getString(R.string.error)}: ${response.message()}")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onPinChange() {
        healthcareViewModel.getPatientSickLeaves(ESickRequest(pin = getStoredPin()))
        binding.wrapper.visibility = View.GONE
        showHideShimmer(true)
    }

    override fun onPinDismiss() {
        showHideShimmer(false)
        binding.clWrapperData.visibility = View.VISIBLE
        binding.reciclerViewESickLeave.visibility = View.GONE
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }
}