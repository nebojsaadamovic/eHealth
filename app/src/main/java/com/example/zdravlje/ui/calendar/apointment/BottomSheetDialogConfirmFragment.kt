package com.example.zdravlje.ui.calendar.apointment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.response.calendar.TimeslotRequest
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.ui.calendar.CalendarFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.calendar.medicament.withoutrecipe.PeriodFromToFragment
import com.example.zdravlje.ui.calendar.personalActivity.PersonalActivityDateFragment
import com.example.zdravlje.ui.healthcare.HealthcareFragment
import com.example.zdravlje.utilities.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetDialogConfirmBinding

class BottomSheetDialogConfirmFragment(
    private var timeSlotId: String? = null,
    private val  className: String?=null
) : BaseBottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetDialogConfirmBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var bundle: Bundle? = null
    private var header: String? = null
    private var headerTitle: String? = null
    private var content: String? = null
    private var activityName: String? = null
    private var title: String? = null
    private var event: Event? = null

    private var listener: OnDeleteListener? = null

    interface OnDeleteListener {
        fun onDelete()
    }

    fun setDeleteListener(listener: OnDeleteListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDialogConfirmBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()

        title = arguments?.getString("title")
        header = arguments?.getString("header")
        headerTitle = arguments?.getString("header_title")
        content = arguments?.getString("content")
        activityName = arguments?.getString("activityName")
        bundle = arguments?.getBundle("bundle")
        event = arguments?.getSerializable("event") as? Event

        if (title == "drugConsumptionActivity") {
            setupUIDrugConsumption()
        }
        else if (title == "PersonalDataActivity_EditArray") {
           setupUIPersonalActivity()
        }
        else if (title == "UserInfoFragment") {
            setupUIUserInfoFragment()
        }
        else {
            setupUI()
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

    private fun setupUI() {
        binding.mbContinue.setOnClickListener {
            getScheduleAppointment()
        }

        binding.mbCancel.setOnClickListener() {
            dismiss()
        }
    }

    private fun setupUIDrugConsumption() {
        binding.mtvHeaderTitle.text = header
        binding.mtvConfirmText.text = content

        binding.mbContinue.setOnClickListener {
            val periodFromToFragment = PeriodFromToFragment()
            bundle?.putString("header_title", headerTitle)
            periodFromToFragment.arguments = bundle
            setCurrentFragment(periodFromToFragment)
            dismiss()
        }

        binding.mbCancel.setOnClickListener() {
            dismiss()
        }
    }

    private fun setupUIPersonalActivity() {
        binding.mtvHeaderTitle.text = header
        binding.mtvConfirmText.text = content

        binding.mbContinue.setOnClickListener {
            val fragament = PersonalActivityDateFragment(
                activityName.toString(),
                event,
                title
            )

            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.activity_personal_fragment_container, fragament)
                commit()
            }

            dismiss()
        }

        binding.mbCancel.setOnClickListener() {
            dismiss()
        }
    }

    private fun setupUIUserInfoFragment(){
        binding.clHeader.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red_warning_E75656))
        binding.mtvHeaderTitle.text = headerTitle
        binding.mtvConfirmText.text = content
        binding.mbContinue.text = getString(R.string.fragment_change_profile_bottomsheet_continue_btn)

        binding.mbContinue.setOnClickListener {
            listener?.onDelete()
            dismiss()
        }

        binding.mbCancel.setOnClickListener() {
            dismiss()
        }
    }

    private fun getScheduleAppointment() {
        val request = timeSlotId?.let { TimeslotRequest(it, getStoredPin()) }
        if (request != null) {
            calendarViewModel.scheduleAppointment(request)
        }
    }

    private fun subscribeToObservers() {
        calendarViewModel.getScheduleAppointmentLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    if (className == "HealthcareFragment") {
                        CommonUtils.openFragmentHome(requireActivity(), HealthcareFragment())
                    } else {
                        CommonUtils.openFragmentCalendar(requireActivity(), CalendarFragment())
                    }

                    dismiss()
                    Toast.makeText(
                        requireContext(),
                        "${getString(R.string.toast_data_save)}: $data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.toast_error_answer)}: ${response.code()}",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }

        calendarViewModel.getScheduleAppointmentErrorLiveData.observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    fun setCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.drug_fragment_container, fragment)
            commit()
        }
    }
}