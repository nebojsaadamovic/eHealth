package com.example.zdravlje.ui.calendar

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.calendar.RealizeEventRequest
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.notification.NotificationResponse
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.utilities.DateUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetEventInfoBinding

class BottomSheetEventInfoFragment(
    var event: Event,
    private var notificationItem: NotificationResponse? = null,
    var msg: String? = null
) : BaseBottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetEventInfoBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var listener: OnCalendarChangeListener? = null

    interface OnCalendarChangeListener {
        fun onCalendarChange(eventId: Int, eventType: Int)
    }

    fun setCalendarChangeListener(listener: OnCalendarChangeListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetEventInfoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
        subscribeToObservers()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    private fun setupUI() {
        if (msg == "Notification") {
            val notificationResponse: NotificationResponse? = notificationItem
            val eventPayload = notificationResponse?.notification?.getEventPayload()
            val doziranje = eventPayload?.event?.eveOnClickPayload?.mandatory?.doziranje
            binding.tvDoseName.text = doziranje

            val dateAndTime =
                notificationItem?.notification?.getEventPayload()?.event?.eveStartTime?.toLong()
                    ?.let {
                        DateUtils.formatTimestampToYYYYMMddHHmmMiddleLine(it)
                    }

            binding.tvDateName.text = dateAndTime?.let { DateUtils.getFormattedDate(it) }
            binding.tvTimeName.text = dateAndTime?.let { DateUtils.getHoursFromDate(it) }

        } else {

            println(notificationItem)
            val gson = Gson()
            val medicineEventPayload =
                gson.fromJson(event.eventOnClickPayload, MedicineEventPayload::class.java)

            val doziranje = medicineEventPayload.mandatory?.doziranje ?: "N/A"
            binding.tvDoseName.text = doziranje
            binding.tvDateName.text = event.eventStartTime?.let { DateUtils.getFormattedDate(it) }
            binding.tvTimeName.text = event.eventStartTime?.let { DateUtils.getHoursFromDate(it) }
        }

        binding.tvMedicineName.text = event.eventTitle

        binding.mbYesBtn.setOnClickListener(View.OnClickListener {
            if (isPinSet()){
                val realizeEventRequest = RealizeEventRequest(event.eventId, 1, getStoredPin())
                calendarViewModel.realizeEvent(realizeEventRequest)
                event.eventId?.let { it1 -> listener?.onCalendarChange(it1, 1) }
            }else{
                Toast.makeText(requireActivity(), R.string.error, Toast.LENGTH_SHORT).show()
            }

        })

        binding.mbNoBtn.setOnClickListener(View.OnClickListener {
                if (isPinSet()){
                    val realizeEventRequest = RealizeEventRequest(event.eventId, 2, getStoredPin())
                    calendarViewModel.realizeEvent(realizeEventRequest)
                    event.eventId?.let { it1 -> listener?.onCalendarChange(it1, 2) }
                }else{
                    Toast.makeText(requireActivity(), R.string.error, Toast.LENGTH_SHORT).show()
                }

        })

        binding.mbCancelBtn.setOnClickListener() {
            dismiss()
        }
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
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                dismiss()
            }
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
}