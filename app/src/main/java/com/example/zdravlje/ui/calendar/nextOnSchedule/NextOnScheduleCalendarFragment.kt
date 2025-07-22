package com.example.zdravlje.ui.calendar.nextOnSchedule

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.dashboard.event_payload.AppointmentEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.CaseEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.TherapyEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.ui.adaptersAndViewHolders.calendar.CalendarActivityAdapter
import com.example.zdravlje.ui.calendar.BottomSheetEventInfoFragment
import com.example.zdravlje.ui.home.dashboard.event_details.AvaliableTherapyInPharmacyActivity
import com.example.zdravlje.ui.home.dashboard.event_details.DoctorsAppointmentFragment
import com.example.zdravlje.ui.home.dashboard.event_details.DrugConsumptionActivity
import com.example.zdravlje.ui.home.dashboard.event_details.PersonalDataActivity
import com.example.zdravlje.ui.home.dashboard.event_details.RealisedTherapyInPharmacyActivity
import com.example.zdravlje.ui.home.dashboard.event_details.RealisedVisitedDoctorActivity
import com.example.zdravlje.ui.home.dashboard.event_details.ScheduledCallInfoFragment
import com.example.zdravlje.ui.interfaces.ActivityClickListener
import com.example.zdravlje.utilities.DateUtils
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentNextOnScheduleCalendarBinding

class NextOnScheduleCalendarFragment() : Fragment(), ActivityClickListener,
    BottomSheetEventInfoFragment.OnCalendarChangeListener,
    BottomSheetRealizedUnrealizedActivityFragment.OnValueChangeListener {

    lateinit var binding: FragmentNextOnScheduleCalendarBinding
    private lateinit var eventsAdapter: CalendarActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNextOnScheduleCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? NextOnScheduleActivity)?.dailyEventsLiveData?.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                getDailyData()
            }
        }

    }

    private fun getDailyData() {
        val acitity = requireActivity() as NextOnScheduleActivity

        eventsAdapter = acitity.getDailyEvents()?.let { CalendarActivityAdapter(it.events, this) }!!
        binding.eventsRecyclerView.layoutManager =
            LinearLayoutManager(context)
        binding.eventsRecyclerView.adapter = eventsAdapter

        binding.dateTextView.text =
            DateUtils.getDateWithWeekName(acitity.getDailyEvents()!!.eventDate)

        binding.clBack.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }

    override fun onActivityClicked(event: Event) {
        val gson = Gson()
        val eveOnClickPayload = event.eventOnClickPayload

        when (event.eventTypeId) {
            1 -> {
                if (event.eventRealized == 0 && event.isPastTime()) {
                    val bottomSheetEventInfo = BottomSheetEventInfoFragment(event)
                    bottomSheetEventInfo.setCalendarChangeListener(this)
                    bottomSheetEventInfo.show(parentFragmentManager, bottomSheetEventInfo.tag)
                } else {
                    val medicineEventPayload =
                        gson.fromJson(eveOnClickPayload, MedicineEventPayload::class.java)

                    //   startNewFragment(DrugConsumptionFragment(medicineEventPayload, event))
                    val intent = Intent(requireActivity(), DrugConsumptionActivity::class.java)
                    intent.putExtra("medicineEventPayload", medicineEventPayload)
                    intent.putExtra("event", event)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }

            2 -> {

                    val appointmentEventPayload =
                        gson.fromJson(eveOnClickPayload, AppointmentEventPayload::class.java)
                    val bundle = Bundle().apply {
                        putParcelable("doctors_appointment_details", appointmentEventPayload)
                        putSerializable("event", event)
                        putString("class_name", "NextOnScheduleCalendarFragment")
                    }

                    val fragment = DoctorsAppointmentFragment()
                    startNewFragment(fragment)
                    fragment.arguments = bundle
            }

            3 -> {
                val therapyEventPayload =
                    gson.fromJson(eveOnClickPayload, TherapyEventPayload::class.java)
                if (event.isPastTime() && !event.isToday()) {
                    val intent =
                        Intent(requireActivity(), RealisedTherapyInPharmacyActivity::class.java)
                    intent.putExtra("therapyEventPayload", therapyEventPayload)
                    intent.putExtra("event", event)
                    startActivity(intent)
                } else {
                    val intent =
                        Intent(requireActivity(), AvaliableTherapyInPharmacyActivity::class.java)
                    intent.putExtra("therapyEventPayload", therapyEventPayload)
                    intent.putExtra("event", event)
                    startActivity(intent)
                }
            }

            4 -> {
                val keyHeaderTitle = getString(R.string.fragment_calendar_appointment_examination_personal_activity)
                val keyTitle = getString(R.string.fragment_calendar_appointment_examination_content)
                if (event.eventRealized == 0 && event.isPastTime()) {
                    val bottomSheet = BottomSheetRealizedUnrealizedActivityFragment(event)
                    bottomSheet.apply {
                        arguments = Bundle().apply {
                            putString("KEY_HEADER_TITLE", keyHeaderTitle)
                            putString("KEY_TITLE", keyTitle)
                            putString("KEY_NAME", keyHeaderTitle)
                        }
                    }
                    bottomSheet.setValueChange(this)
                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                } else {
                    val intent = Intent(requireActivity(), PersonalDataActivity::class.java)
                    intent.putExtra("event", event)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }

            6 -> {
                val caseEventPayload =
                    gson.fromJson(eveOnClickPayload, CaseEventPayload::class.java)

                val intent =
                    Intent(requireActivity(), RealisedVisitedDoctorActivity::class.java)
                intent.putExtra("caseEventPayload", caseEventPayload)
                intent.putExtra("event", event)
                startActivity(intent)
            }

            7 -> {
                val title = getString(R.string.fragment_calendar_appointment_examination_header_title)
                val className = "NextOnScheduleCalendarFragment"
                val fragment = ScheduledCallInfoFragment.newInstance(event, null, title, className)
                startNewFragment(fragment)
            }
        }
    }

    override fun onCalendarChange(eventId: Int, eventType: Int) {
        val updatedEvent = eventsAdapter.activities.find { it.eventId == eventId }
        updatedEvent?.let {
            it.eventRealized = eventType
            eventsAdapter.updateEvent(it)
        }
    }

    override fun onValueChange(eventId: Int, eventType: Int) {
        val updatedEvent = eventsAdapter.activities.find { it.eventId == eventId }
        updatedEvent?.let {
            it.eventRealized = eventType
            eventsAdapter.updateEvent(it)
        }
    }

    fun startNewFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.home_fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}