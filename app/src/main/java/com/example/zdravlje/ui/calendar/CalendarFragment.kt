package com.example.zdravlje.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.dashobard.EventsAndActivitiesRequest
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.model.response.dashboard.event_payload.AppointmentEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.CaseEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.TherapyEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.calendar.CalendarAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.NextOnScheduleActivity
import com.example.zdravlje.ui.calendar.options.CalendarOptionsActivity
import com.example.zdravlje.ui.home.dashboard.event_details.AvaliableTherapyInPharmacyActivity
import com.example.zdravlje.ui.home.dashboard.event_details.DrugConsumptionActivity
import com.example.zdravlje.ui.home.dashboard.event_details.PersonalDataActivity
import com.example.zdravlje.ui.home.dashboard.event_details.RealisedTherapyInPharmacyActivity
import com.example.zdravlje.ui.home.dashboard.event_details.RealisedVisitedDoctorActivity
import com.example.zdravlje.ui.interfaces.ActivityClickListener
import com.example.zdravlje.ui.notification.NotificationActivity
import com.example.zdravlje.ui.notification.NotificationViewModel
import com.example.zdravlje.ui.notification.NotificationViewModelFactory
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.DateUtils
import com.example.zdravlje.utilities.loadImage
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

class CalendarFragment : BaseFragment(), ActivityClickListener,
    BottomSheetFilterFragment.OnFilterListener {

    lateinit var binding: FragmentCalendarBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var monthTextView: TextView
    private var currentMonth = 0
    private var currentYear = 0
    private var userPin: String? = null
    private lateinit var eventsAndActivitiesRequest: EventsAndActivitiesRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideShimmer(true)
        subscribeToObservers()
        observePin()
        setupUI()

        calendarRecyclerView = binding.calendarRecyclerView
        monthTextView = binding.monthTextView // Dodaj TextView umesto Spinner-a
        setupMonthChangeButtons()

        updateMonthTextView() // Postavi početni mesec u TextView
        //  updateCalendar()
    }

    private fun observePin() {
            userPin = getStoredPin()
            notificationCount()
    }

    private fun setupUI() {
        val calendar = Calendar.getInstance()
        currentMonth = calendar.get(Calendar.MONTH)
        currentYear = calendar.get(Calendar.YEAR)

        val (startDate, endDate) = getStartAndEndDateForSelectedMonth()

        eventsAndActivitiesRequest = EventsAndActivitiesRequest(
            startDate,
            endDate,
            sort_in = "ASC",
            limit_in = 100000,
            mobile_group = 1
        )

        eventsAndActivitiesRequest.pin = getStoredPin()
        calendarViewModel.getCalendarEventsForMonth(
            eventsAndActivitiesRequest
        )

        binding.ivFilter.setOnClickListener(View.OnClickListener {
            val bottomSheetFilterFragment = BottomSheetFilterFragment()
            bottomSheetFilterFragment.setOnFilterListener(this)
            bottomSheetFilterFragment.show(parentFragmentManager, bottomSheetFilterFragment.tag)
        })

        binding.ivAdd.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), CalendarOptionsActivity::class.java)
            startActivity(intent)
        })

        binding.ivNotification.setOnClickListener() {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }
    }

    private fun notificationCount() {
        notificationViewModel.notificationCount(
            NotificationReadRequest(
                pin = userPin,
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

    private fun updateMonthTextView() {
        val appLocale = CommonUtils.getCurrentAppLocale()
        val monthName = YearMonth.of(currentYear, currentMonth + 1)
            .month
            .getDisplayName(TextStyle.FULL, appLocale)

        monthTextView.text = "$monthName $currentYear"
    }

    private fun subscribeToObservers() {
        calendarViewModel.getCalendarEventsForMonthResponseLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                showHideShimmer(false)
                val dailyEventsList = response.body() ?: emptyList()
                val eventsMap = dailyEventsList.associateBy { it.eventDate }
                updateCalendar(eventsMap)
            } else {
                showHideShimmer(false)
                updateCalendar(emptyMap())
            }
        }

        calendarViewModel.getCalendarEventsForMonthErrorLiveData.observe(viewLifecycleOwner) {
            showHideShimmer(false)
            Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
        }

        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            val userData = userInfo?.body()

            val avatarValue = userData?.avt_encoded_value
            val fullName = userData?.sgco_name ?: ""

            if (!avatarValue.isNullOrEmpty()) {
                binding.ivUserImage.visibility = View.VISIBLE
                binding.tvAvatarInitials.visibility = View.GONE
                binding.ivUserImage.loadImage(
                    avatarValue,
                    R.drawable.ic_contact_avatar
                )
            } else {
                if (userData?.pat_sex == 2) {
                    binding.tvAvatarInitials.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.screeningColor)
                    )
                } else {
                    binding.tvAvatarInitials.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                    )
                }

                binding.ivUserImage.visibility = View.GONE
                binding.tvAvatarInitials.visibility = View.VISIBLE
                binding.tvAvatarInitials.text = CommonUtils.getInitials(fullName)
            }
        }
    }

    private fun updateCalendar(eventsMap: Map<String, DailyEvents>) {
        val days = mutableListOf<Pair<String, Boolean>>()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, currentMonth)
            set(Calendar.YEAR, currentYear)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val emptyCells = when (firstDayOfWeek) {
            Calendar.SUNDAY -> 6
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 0
        }

        if (emptyCells > 0) {
            val previousMonthCalendar = Calendar.getInstance().apply {
                set(Calendar.MONTH, currentMonth)
                set(Calendar.YEAR, currentYear)
                add(Calendar.MONTH, -1)
            }
            val daysInPreviousMonth = previousMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            for (i in 0 until emptyCells) {
                days.add(Pair((daysInPreviousMonth - emptyCells + i + 1).toString(), false))
            }
        }

        for (day in 1..daysInMonth) {
            days.add(Pair(day.toString(), true))
        }

        val lastDayOfWeek = (emptyCells + daysInMonth) % 7
        val cellsToFill = if (lastDayOfWeek > 0) 7 - lastDayOfWeek else 0

        for (i in 1..cellsToFill) {
            days.add(Pair(i.toString(), false))
        }

        calendarAdapter = CalendarAdapter(
            days,
            eventsMap,
            currentMonth,
            currentYear,
            object : CalendarAdapter.OnEventClickListener {
                override fun onEventClick(dailyEvents: DailyEvents) {

                    val gson = Gson()
                    val jsonObjectString = gson.toJson(dailyEvents)
                    val intent = Intent(requireActivity(), NextOnScheduleActivity::class.java)
                    intent.putExtra("jsonObject", jsonObjectString)
                    startActivity(intent)
                }
            })
        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun setupMonthChangeButtons() {
        binding.buttonPreviousMonth.setOnClickListener {
            if (currentMonth > 0) {
                currentMonth--
            } else {
                currentMonth = 11
                currentYear--
            }
            updateMonthTextView()
            val (startDate, endDate) = getStartAndEndDateForSelectedMonth()

            eventsAndActivitiesRequest = EventsAndActivitiesRequest(
                startDate,
                endDate,
                sort_in = "ASC",
                limit_in = 100000,
                mobile_group = 1
            )

                eventsAndActivitiesRequest.pin = getStoredPin()
                calendarViewModel.getCalendarEventsForMonth(
                    eventsAndActivitiesRequest
                )

        }

        binding.buttonNextMonth.setOnClickListener {
            if (currentMonth < 11) {
                currentMonth++
            } else {
                currentMonth = 0
                currentYear++
            }
            updateMonthTextView()
            val (startDate, endDate) = getStartAndEndDateForSelectedMonth()

            eventsAndActivitiesRequest = EventsAndActivitiesRequest(
                startDate,
                endDate,
                sort_in = "ASC",
                limit_in = 100000,
                mobile_group = 1
            )

            eventsAndActivitiesRequest.pin = getStoredPin()
            calendarViewModel.getCalendarEventsForMonth(
                eventsAndActivitiesRequest
            )
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

        val notificationRepository = NotificationRepository()
        val notificationViewModelFactory =
            NotificationViewModelFactory(notificationRepository, dataStoreRepository)
        notificationViewModel = ViewModelProvider(requireActivity(), notificationViewModelFactory)
            .get(NotificationViewModel::class.java)

        val profileRepository = ProfileRepository()
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)
    }


    private fun getStartAndEndDateForSelectedMonth(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Postavi na prvi dan mjeseca u ponoć
        calendar.set(Calendar.YEAR, currentYear)
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = dateFormat.format(calendar.time)

        // Postavi na posljednji dan mjeseca u ponoć
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    override fun onActivityClicked(event: Event) {
        val gson = Gson()
        val eveOnClickPayload = event.eventOnClickPayload

        when (event.eventTypeId) {
            1 -> {
                if (event.eventRealized == 0 && event.isToday() && event.isPastTime()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fragment_calendar_display_unrealized_event_in_progress),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val medicineEventPayload =
                        gson.fromJson(eveOnClickPayload, MedicineEventPayload::class.java)
                    val intent = Intent(requireActivity(), DrugConsumptionActivity::class.java)
                    intent.putExtra("medicineEventPayload", medicineEventPayload)
                    intent.putExtra("event", event)
                    startActivity(intent)
                }
            }

            2 -> {
                val appointmentEventPayload =
                    gson.fromJson(eveOnClickPayload, AppointmentEventPayload::class.java)
                Log.i("mand!", appointmentEventPayload.toString())
            }

            3 -> {
                val therapyEventPayload =
                    gson.fromJson(eveOnClickPayload, TherapyEventPayload::class.java)
                // startNewFragment(AvailableTherapyInPharmacyFragment(therapyEventPayload))
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
                if (event.eventRealized == 0 && event.isToday() && event.isPastTime()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fragment_calendar_display_unrealized_event_in_progress),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent = Intent(requireActivity(), PersonalDataActivity::class.java)
                    intent.putExtra("event", event)
                    startActivity(intent)
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
        }
    }

    override fun onResume() {
        super.onResume()
        binding.calendarRecyclerView.smoothScrollToPosition(0)

            eventsAndActivitiesRequest.pin = getStoredPin()
            calendarViewModel.getCalendarEventsForMonth(
                eventsAndActivitiesRequest
            )

    }

    override fun onFilterChange(filter: Int) {
        val tip = when (filter) {
            1 ->  getString(R.string.fragment_bottom_sheet_filter_reminders_therapy_consumption_label)
            2 ->  getString(R.string.fragment_bottom_sheet_filter_scheduled_doctors_appointments_label)
            3 -> getString(R.string.fragment_bottom_sheet_filter_available_therapies_label)
            4 -> getString(R.string.fragment_bottom_sheet_filter_personalized_activities_label)
            5 ->getString(R.string.fragment_bottom_sheet_filter_screening_examinations_label)
            7 -> getString(R.string.fragment_bottom_sheet_filter_scheduled_calls_label)
            -1 ->getString(R.string.fragment_bottom_sheet_filter_show_all)
            else -> filter
        }
        Toast.makeText(context, "Izabrali ste: $tip", Toast.LENGTH_SHORT).show()

        val (startDate, endDate) = getStartAndEndDateForSelectedMonth()
        eventsAndActivitiesRequest = EventsAndActivitiesRequest(
            startDate,
            endDate,
            sort_in = "ASC",
            limit_in = 100000,
            mobile_group = 1,
            eve_event_type_id = filter
        )

        eventsAndActivitiesRequest.pin = getStoredPin()
        calendarViewModel.getCalendarEventsForMonth(
            eventsAndActivitiesRequest
        )
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }
}
