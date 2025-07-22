package com.example.zdravlje.ui.home.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.dashobard.EventsAndActivitiesRequest
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.repository.DashboardRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.home.ActivitiesAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.options.CalendarOptionsActivity
import com.example.zdravlje.ui.home.DashboardViewModel
import com.example.zdravlje.ui.home.DashboardViewModelFactory
import com.example.zdravlje.ui.interfaces.ActivityClickListener
import com.example.zdravlje.utilities.DateUtils
import com.example.zdravlje.utilities.DateUtils.checkIsFirstNextEvent
import com.example.zdravlje.utilities.DateUtils.compareWithCurrentDate
import com.example.zdravlje.utilities.DateUtils.getHoursFromDate
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentActivitiesBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ActivitiesFragment : BaseFragment() {
    private val dashboardViewModel: DashboardViewModel by activityViewModels {
        DashboardViewModelFactory(
            DashboardRepository(),
            DataStoreRepository(requireActivity())
        )
    }

    private lateinit var binding: FragmentActivitiesBinding
    private var clickListener: ActivityClickListener? = null
    private lateinit var nextActivities: DailyEvents
    private lateinit var eventsAndActivitiesRequest: EventsAndActivitiesRequest
    private var eventsAtSameTime : Int?=null
    private var eventFuture: Event?=null
    var dateValue: String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        subscribeToObservers()
    }

    private fun subscribeToObservers() {

        dashboardViewModel.errorLiveData.observe(viewLifecycleOwner){
            showErrorDialog()
        }

        dashboardViewModel.dateParem.observe(viewLifecycleOwner) { selectedDate ->
            if (!selectedDate.isNullOrEmpty()) {
                dateValue = selectedDate
                filterActivitiesByDate()
            }
        }


        dashboardViewModel.dashboardFutureResponseLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                nextActivities = response.body()!![0]
                binding.shimmer.visibility = View.GONE

                val eventsAdapter = ActivitiesAdapter(
                    response.body()!!,
                    clickListener,
                    requireActivity(),
                    dashboardViewModel
                )

                binding.eventActivityRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.eventActivityRecyclerView.adapter = eventsAdapter
                binding.eventActivityRecyclerView.visibility = View.VISIBLE

                if (compareWithCurrentDate(nextActivities.eventDate)) {
                    val groupedByTime = nextActivities.events.groupBy { it.eventStartTime }

                    for (event in nextActivities.events) {
                        if (event.eventStartTime?.let { checkIsFirstNextEvent(it) } == true) {

                            eventsAtSameTime = groupedByTime[event.eventStartTime]?.size ?: 0

                            if (eventsAtSameTime!! > 1) {
                                binding.titleNextOnSchedule.text = getString(R.string.fragment_activities_multiple_activities_at_same_time)
                            } else {
                                eventFuture = event
                                when (event.eventTypeId) {
                                    1 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_consumption_medicamente)

                                    2 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_doctors_appointment)

                                    3 -> binding.titleNextOnSchedule.text =
                                        if (event.isPastTime() && !event.isToday()) {
                                            getString(R.string.calendar_activity_adapter_realized_e_recipe)
                                        } else {
                                            getString(R.string.calendar_activity_adapter_available_therapy_pharmacy)
                                        }

                                    4 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_personal_activity)

                                    5 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_appointment_screening_examination)

                                    6 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_completed_doctor_visit)

                                    7 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_scheduled_call)
                                }
                            }

                            binding.mtvEventTimeDesc.text = event.eventStartTime?.let {
                                getHoursFromDate(it)
                            }

                            binding.nextActivities.visibility = View.VISIBLE
                            break
                        }
                    }
                } else {
                    binding.nextActivities.visibility = View.GONE
                }
            } else {
                binding.shimmer.visibility = View.GONE
                binding.tvDescDate.text =
                    DateUtils.getDateWithWeekName(DateUtils.getCurrentDateTime())
                binding.clErrorWrapper.visibility = View.VISIBLE
            }
        }

        dashboardViewModel.dashboardFutureResponseErrorLiveData.observe(viewLifecycleOwner) {
            binding.shimmer.visibility = View.GONE
            binding.tvDescDate.text = DateUtils.getDateWithWeekName(DateUtils.getCurrentDateTime())
            binding.clErrorWrapper.visibility = View.VISIBLE
        }
    }

    private fun setupUI() {
        eventsAndActivitiesRequest = EventsAndActivitiesRequest(
            DateUtils.getCurrentDateTimeFromMidnight(),
            DateUtils.getSixMonthsLaterDateTime(),
            sort_in = "ASC",
            limit_in = 100000,
            mobile_group = 1
        )

        eventsAndActivitiesRequest.pin = getStoredPin()
        dashboardViewModel.getFutureDashboardData(eventsAndActivitiesRequest)


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.eventActivityRecyclerView.visibility = View.GONE
            binding.shimmer.visibility = View.VISIBLE
            dashboardViewModel.getFutureDashboardData(eventsAndActivitiesRequest)
        }

        binding.nextActivities.setOnClickListener {
            if (eventsAtSameTime!! > 1) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    .replace(
                        R.id.home_fragment_container_view,
                        NextOnSchedule(
                            getString(R.string.fragment_activity_next_scheduled),
                            nextActivities,
                            clickListener!!
                        )
                    )
                    .addToBackStack(null)
                    .commit()
            }else{
                eventFuture?.let { it1 -> clickListener?.onActivityClicked(it1) }
            }
        }

        binding.mcvMedicine.setOnClickListener {
            val intent = Intent(context, CalendarOptionsActivity::class.java).apply {
                putExtra("fragment_option", CalendarOptionsActivity.CalendarOptionsFragments.ADD_MEDICAMENTE_FROM_E_RECIPE)
            }
            startActivity(intent)
        }

        binding.mcvAppointment.setOnClickListener {
            val intent = Intent(context, CalendarOptionsActivity::class.java).apply {
                putExtra("fragment_option", CalendarOptionsActivity.CalendarOptionsFragments.SCHEDULE_EXAMINATION)
            }
            startActivity(intent)
        }

        binding.mcvActivity.setOnClickListener {
            val intent = Intent(context, CalendarOptionsActivity::class.java).apply {
                putExtra("fragment_option", CalendarOptionsActivity.CalendarOptionsFragments.PERSONAL_ACTIVITY)
            }
            startActivity(intent)
        }
        binding.mcvScreening.setOnClickListener {
            val intent = Intent(context, CalendarOptionsActivity::class.java).apply {
                putExtra("fragment_option", CalendarOptionsActivity.CalendarOptionsFragments.APPLICATION_FOR_SCREENING)
            }
            startActivity(intent)
        }
    }

    fun refreshData() {
        lifecycleScope.launch {
            binding.eventActivityRecyclerView.visibility = View.GONE
            binding.shimmer.visibility = View.VISIBLE
            delay(1000)
            eventsAndActivitiesRequest.pin = getStoredPin()
            dashboardViewModel.getFutureDashboardData(eventsAndActivitiesRequest)

        }
    }

    fun setClickListener(listener: ActivityClickListener) {
        this.clickListener = listener
    }

    companion object {
        fun newInstance(): ActivitiesFragment {
            return ActivitiesFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.shimmer.visibility = View.GONE
        binding.clErrorWrapper.visibility = View.GONE
        refreshData()
    }

    private fun filterActivitiesByDate(){
        dashboardViewModel.dashboardFutureResponseLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {

                val dateByCalendar: String? = dateValue?.let {
                    DateUtils.convertVerLineddMyyyyToHorLineyyyyMMdd(it)
                }

                if (!dateByCalendar.isNullOrEmpty()) {
                    val selectedDate = LocalDate.parse(dateByCalendar, DateTimeFormatter.ISO_DATE)
                    val today = LocalDate.now()

                    if (selectedDate.isBefore(today)) {
                        Toast.makeText(requireContext(), "Nije moguće filtrirati podatke u prošlosti", Toast.LENGTH_SHORT).show()
                        return@observe
                    }
                }

                val dailyEventsList = response.body()

                val filteredDailyEvents: DailyEvents? = if (!dateByCalendar.isNullOrEmpty()) {
                    dailyEventsList?.firstOrNull { it.eventDate == dateByCalendar }
                } else null

                nextActivities = filteredDailyEvents ?: dailyEventsList!![0]

                binding.shimmer.visibility = View.GONE

                val filteredList = if (filteredDailyEvents != null) listOf(filteredDailyEvents) else dailyEventsList!!

                val eventsAdapter = ActivitiesAdapter(filteredList, clickListener, requireActivity(), dashboardViewModel)

                binding.eventActivityRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.eventActivityRecyclerView.adapter = eventsAdapter
                binding.eventActivityRecyclerView.visibility = View.VISIBLE

                if (compareWithCurrentDate(nextActivities.eventDate)) {
                    val groupedByTime = nextActivities.events.groupBy { it.eventStartTime }

                    for (event in nextActivities.events) {
                        if (event.eventStartTime?.let { checkIsFirstNextEvent(it) } == true) {

                            eventsAtSameTime = groupedByTime[event.eventStartTime]?.size ?: 0

                            if (eventsAtSameTime!! > 1) {
                                binding.titleNextOnSchedule.text = getString(R.string.fragment_activities_multiple_activities_at_same_time)
                            } else {
                                eventFuture = event
                                when (event.eventTypeId) {
                                    1 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_consumption_medicamente)

                                    2 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_doctors_appointment)

                                    3 -> binding.titleNextOnSchedule.text =
                                        if (event.isPastTime() && !event.isToday()) {
                                            getString(R.string.calendar_activity_adapter_realized_e_recipe)
                                        } else {
                                            getString(R.string.calendar_activity_adapter_available_therapy_pharmacy)
                                        }

                                    4 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_personal_activity)

                                    5 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_appointment_screening_examination)

                                    6 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_completed_doctor_visit)

                                    7 -> binding.titleNextOnSchedule.text =
                                        getString(R.string.calendar_activity_adapter_scheduled_call)
                                }
                            }

                            binding.mtvEventTimeDesc.text = event.eventStartTime?.let {
                                getHoursFromDate(it)
                            }

                            binding.nextActivities.visibility = View.VISIBLE
                            break
                        }
                    }
                } else {
                    binding.nextActivities.visibility = View.GONE
                }
            } else {
                binding.shimmer.visibility = View.GONE
                binding.tvDescDate.text =
                    DateUtils.getDateWithWeekName(DateUtils.getCurrentDateTime())
                binding.clErrorWrapper.visibility = View.VISIBLE
            }
        }
    }
}