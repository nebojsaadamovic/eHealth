package com.example.zdravlje.ui.home.dashboard.event_details_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.notification.Event
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.databinding.FragmentDrugConsumptionBinding

class DrugConsumptionFragment: Fragment() {

    lateinit var binding: FragmentDrugConsumptionBinding
    private var medicineEventPayload: MedicineEventPayload? = null
    private var event: Event? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDrugConsumptionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            medicineEventPayload = bundle.getSerializable("medicineEventPayload") as? MedicineEventPayload
            event = bundle.getSerializable("eventNotification") as? Event
        }
        setupUI()
    }

    fun setupUI() {
        val startDateAndTime =
            event?.eveStartTime?.toLong()
                ?.let {
                    DateUtils.formatTimestampToYYYYMMddHHmmMiddleLine(it)
                }
        val endDateAndTime =
            event?.eveEndTime?.toLong()
                ?.let {
                    DateUtils.formatTimestampToYYYYMMddHHmmMiddleLine(it)
                }
        binding.dateTitle.text = event?.eveStartTime.toString()
        binding.mtvNameValue.text = event?.eveTitle
        binding.mtvDescValue.text = medicineEventPayload?.mandatory?.doziranje
        binding.mtvTimeFrom.text = startDateAndTime?.let { DateUtils.getFormattedDate(it) }
        binding.mtvTimeTo.text = endDateAndTime?.let { DateUtils.getFormattedDate(it) }
        binding.mtvTimeConsumptionValue.text = startDateAndTime?.let { DateUtils.getHoursFromDate(it) }
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }
}