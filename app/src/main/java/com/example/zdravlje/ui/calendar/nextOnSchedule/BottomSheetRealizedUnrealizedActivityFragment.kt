package com.example.zdravlje.ui.calendar.nextOnSchedule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.calendar.RealizeEventRequest
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.utilities.DateUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetRealizedUnrealizedActivityBinding

class BottomSheetRealizedUnrealizedActivityFragment(
    var event: Event? = null,
    var time: String? = null
) : BaseBottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetRealizedUnrealizedActivityBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    private var listener: OnValueChangeListener? = null

    interface OnValueChangeListener {
        fun onValueChange(eventId: Int, eventType: Int)
    }

    fun setValueChange(listener: OnValueChangeListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetRealizedUnrealizedActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headreTitle = arguments?.getString("KEY_HEADER_TITLE")
        val title = arguments?.getString("KEY_TITLE")
        val name = arguments?.getString("KEY_NAME")

        if (title != null) {
            binding.mtvTitle.visibility = View.VISIBLE
            binding.mtvTitle.text = title
        }
        binding.mvHeaderTitle.text = headreTitle
        binding.tvPersonalActivity.text = name
        setViewModels()
        val keyHeaderTitle=getString(R.string.fragment_calendar_appointment_examination_personal_activity)
        if (headreTitle == keyHeaderTitle) {
            binding.mtvTitleSecond.text = title
            binding.clContentSecond.visibility = View.VISIBLE
            binding.clContent.visibility = View.GONE
            setupUISecond()
        }
        else {
            setupUI()
        }

        subscribeToObservers()
    }

    private fun setupUISecond() {
        binding.tvPersonalActivityValue.text = event?.eventTitle ?: ""
        binding.tvDescriptionValue.text = event?.eventDescription
        binding.tvDateValue.text = event?.eventStartTime?.let { DateUtils.getFormattedDate(it) }
        binding.tvTimeValue.text = event?.eventStartTime?.let { DateUtils.getHoursFromDate(it) }
        binding.mbYesBtn.setOnClickListener(View.OnClickListener {
            lifecycleScope.launch {
                val realizeEventRequest = RealizeEventRequest(event?.eventId, 1, getStoredPin())
                calendarViewModel.realizeEvent(realizeEventRequest)
                event?.eventId?.let { it1 -> listener?.onValueChange(it1, 1) }

            }
        })

        binding.mbNoBtn.setOnClickListener(View.OnClickListener {
            val realizeEventRequest = RealizeEventRequest(event?.eventId, 2, getStoredPin())
            calendarViewModel.realizeEvent(realizeEventRequest)
            event?.eventId?.let { it1 -> listener?.onValueChange(it1, 2)
            }
        })

        binding.mbCancelBtn.setOnClickListener() {
            dismiss()
        }
    }

    private fun setupUI() {
        binding.tvPersonalActivityValue.text = event?.eventTitle ?: ""
        binding.tvDescriptionValue.text = event?.eventDescription
        binding.tvDateValue.text = event?.eventStartTime?.let { DateUtils.getFormattedDate(it) }
        binding.tvTimeValue.text = event?.eventStartTime?.let { DateUtils.getHoursFromDate(it) }

        binding.mbConfirm.setOnClickListener(View.OnClickListener {
            val realizeEventRequest = RealizeEventRequest(event?.eventId, 1, getStoredPin())
            calendarViewModel.realizeEvent(realizeEventRequest)
            event?.eventId?.let { it1 -> listener?.onValueChange(it1, 1) }

        })

        binding.mbCancel.setOnClickListener(View.OnClickListener {
            val realizeEventRequest = RealizeEventRequest(event?.eventId, 2, getStoredPin())
            calendarViewModel.realizeEvent(realizeEventRequest)
            event?.eventId?.let { it1 -> listener?.onValueChange(it1, 2) }
        })
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
        calendarViewModel.realizeEventLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.activity_executed),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            } else {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}