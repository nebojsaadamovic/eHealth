package com.example.zdravlje.ui.home.dashboard.event_details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.chat.ScheduleCallData
import com.example.zdravlje.data.model.request.calendar.DeleteEvent
import com.example.zdravlje.data.model.request.chat.EvePayload
import com.example.zdravlje.data.model.request.chat.EventDetailsRequest
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.dashboard.future.EventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Participant
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.ChatRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.schedulecall.ScheduleCallAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.home.scheduledcall.ScheduledCallParticipantsAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetConfirmDeleteReminderFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.NextOnScheduleActivity
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.ChatViewModelFactory
import com.example.zdravlje.ui.chat.scheduleCall.BottomSheetTimeFromFragment
import com.example.zdravlje.ui.chat.scheduleCall.BottomSheetTimeToFragment
import com.example.zdravlje.ui.chat.scheduleCall.ScheduleCallParticipiantFragment
import com.example.zdravlje.ui.chatAudioAndVideoCall.DemoCallActivity
import com.example.zdravlje.ui.home.HomeActivity
import com.example.zdravlje.ui.notification.NotificationActivity
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.DateUtils
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentScheduledCallInfoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduledCallInfoFragment : BaseFragment(),
    BottomSheetTimeFromFragment.OnBottomSheetTimeFromListener,
    BottomSheetTimeToFragment.OnBottomSheetTimeToListener,
    BottomSheetConfirmDeleteReminderFragment.OnCheckedListener {

    private lateinit var binding: FragmentScheduledCallInfoBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var scheduledCallParticipantsAdapter: ScheduledCallParticipantsAdapter
    private lateinit var scheduleCallAdapter: ScheduleCallAdapter
    private var event: Event? = null
    private var eventNotification: com.example.zdravlje.data.model.response.notification.Event? = null
    private var headerTitle: String? = null
    private var className: String? = null
    private var classFragment: String? = null
    private var vccId: String? = null
    private var currentUser: UserInfo? = null
    private var dataList: List<com.example.zdravlje.data.model.response.chat.Participant> = emptyList()
    private var selectedParticipantsValue: List<com.example.zdravlje.data.model.response.chat.Participant> = emptyList()
    private var participants: List<Participant>? = null
    private var combinedList: List<com.example.zdravlje.data.model.response.chat.Participant> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduledCallInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()

        arguments?.let {
            selectedParticipantsValue = it.getParcelableArrayList("selectedParticipants") ?: emptyList()
            calendarViewModel.selectedParticipants = calendarViewModel.selectedParticipants + selectedParticipantsValue
            if (!selectedParticipantsValue.isEmpty()) binding.mtvSave.visibility = View.VISIBLE

            event = it.getSerializable("event") as? Event
            val gson = Gson()
            val eventPayloadJson = event?.eventOnClickPayload
            val payload: EventPayload? = gson.fromJson(eventPayloadJson, EventPayload::class.java)

            vccId = payload?.vcc_id
            className = it.getString("class_name")

            eventNotification =
                it.getSerializable("eventNotification") as? com.example.zdravlje.data.model.response.notification.Event
            headerTitle = it.getString("header_title")
        }

        profileViewModel.getUserInformation()
        subscribeToObservers()

        if (event == null) {
            profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
                if (userInfo.isSuccessful) {
                    currentUser = userInfo.body()
                    setupUIEventNotification()  // data for NotificationAdapter
                }
            }

        } else {
            profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
                if (userInfo.isSuccessful) {
                    currentUser = userInfo.body()
                    setupUIEventFuture() // data for CalendarAdapter
                }
            }
        }

        setupUICommonOptions()
    }

    private fun setupUICommonOptions() {
        binding.mcvCopyLink.setOnClickListener {
            val textToCopy = binding.mtvCopyLink.text
            copyToClipboard(textToCopy.toString())
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_schedule_call_network_toast_copy_txt),
                Toast.LENGTH_SHORT).show()
        }

        binding.ivDelete.setOnClickListener() {
            val bottomSheet = BottomSheetConfirmDeleteReminderFragment()
            bottomSheet.setCheckedListener(this)
            bottomSheet.setTextSource("DeleteReminder-Scheduled Call")
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupUIEventFuture() {
        event?.let {
            val gson = Gson()
            val eventPayload = it.eventOnClickPayload?.let { payload ->
                gson.fromJson(payload, EventPayload::class.java)
            }

            participants = eventPayload?.participants
            binding.mtvCopyLink.text = "https://hepor.nites.rs/avc/$vccId"

            var creatorParticipant: Int? = null
            participants?.forEach() { participant ->
                if (participant.creator == 1)
                    creatorParticipant = participant.sgco_id?.toInt()
            }

                    if (creatorParticipant == currentUser?.sgco_id) {
                        binding.mbStartCall.setOnClickListener {
                            mainViewModel.startCall(vccId.toString(), false)
                        }
                    } else {
                        binding.mbStartCall.setOnClickListener {
                            mainViewModel.acceptCall(vccId.toString(), false)
                        }
                    }

                    if ((className == "NextOnScheduleCalendarFragment" || className == "ScheduleCallParticipiantFragment")
                        && creatorParticipant == currentUser?.sgco_id
                    ) {
                        binding.mtvTitle.text = getString(R.string.fragment_calendar_title)
                        binding.mbCancelCall.visibility = View.VISIBLE
                        binding.mtvDateValue.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.base_blue_color
                            )
                        )
                        binding.mtvDateValue.isEnabled = true
                        binding.mtvDateValue.setOnClickListener() {
                            DateUtils.showDatePickerDialog(
                                requireContext(),
                                binding.mtvDateValue
                            ) { selectedDate ->
                                binding.mtvDateValue.text =
                                    DateUtils.formatDate_HorizLineddMyyyy_DotddMMyyyy(selectedDate)
                                if(!selectedDate.isNullOrEmpty())
                                    binding.mtvSave.visibility = View.VISIBLE
                            }
                        }

                        binding.mtvTimeFromValue.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.base_blue_color
                            )
                        )

                        binding.mtvTimeFromValue.setOnClickListener() {
                            val bottomSheetTimeFromFragment = BottomSheetTimeFromFragment()
                            bottomSheetTimeFromFragment.setListener(this)
                            bottomSheetTimeFromFragment.show(
                                requireActivity().supportFragmentManager,
                                bottomSheetTimeFromFragment.tag
                            )
                        }

                        binding.mtvTimeToValue.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.base_blue_color
                            )
                        )

                        binding.mtvTimeToValue.setOnClickListener() {
                            val bottomSheetTimeToFragment = BottomSheetTimeToFragment()
                            bottomSheetTimeToFragment.setTargetFragment(this, 0)
                            bottomSheetTimeToFragment.show(
                                requireActivity().supportFragmentManager,
                                bottomSheetTimeToFragment.tag
                            )
                        }

                        if (event?.eventRealized == 0 && event?.isFuture() == true) {
                            binding.ivDelete.visibility = View.VISIBLE
                        }

                        binding.mtvAddParticipiantValue.visibility = View.VISIBLE
                        binding.mtvAddParticipiantValue.setOnClickListener() {
                            val scheduleCallData = ScheduleCallData(
                                binding.mtvDesc.text.toString(),
                                binding.mtvOrganizerNameValue.text.toString(),
                                binding.mtvDateValue.text.toString(),
                                binding.mtvTimeFromValue.text.toString(),
                            )
                            val bundle = Bundle().apply {
                                putParcelable("scheduleCallData", scheduleCallData)
                            }

                            classFragment = "ScheduledCallInfoFragment"
                            if (!combinedList.isNullOrEmpty()){
                                val existingIds =
                                    combinedList?.mapNotNull { it.sgco_id }?.toSet() ?: emptySet()

                                dataList = dataList.filter { it.sgco_id !in existingIds }

                            }else {
                                val existingIds =
                                    participants?.mapNotNull { it.sgco_id?.toInt() }?.toSet() ?: emptySet()

                                dataList = dataList.filter { it.sgco_id !in existingIds }
                            }

                            val fragment = ScheduleCallParticipiantFragment(
                                dataList,
                                classFragment,
                                event
                            ).apply {
                                arguments = bundle
                            }

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.home_fragment_container_view, fragment)
                                .addToBackStack(null).commit()
                        }
                    } else if ((className == "HomeFragment"
                                || className == "NextOnScheduleCalendarFragment"
                                || className == "ScheduleCallParticipiantFragment")
                        && creatorParticipant != currentUser?.sgco_id
                    ) {
                        binding.mbStartCall.text = getString(R.string.fragment_schedule_call_enter_call)
                    }

            if (headerTitle != null) {
                binding.mtvTitle.text = headerTitle
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = sdf.parse(it.eventStartTime)
            val dateFormat = SimpleDateFormat("dd.MM.YYYY", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            binding.mtvDesc.text = it.eventTitle
            binding.mtvDateValue.text = dateFormat.format(date)
            binding.mtvTimeFromValue.text = timeFormat.format(date)
            binding.mtvTimeToValue.text = it.eventEndTime?.let { endTime ->
                val endDate = sdf.parse(endTime)
                timeFormat.format(endDate)
            }

            val creators = participants?.filter { it.creator == 1 }
            val creatorNames = creators?.joinToString(", ") { it.sgco_name }
                ?: getString(R.string.fragment_scheduled_call_info_creator_call)
            binding.mtvOrganizerNameValue.text = creatorNames

            if (calendarViewModel.selectedParticipants.isNotEmpty()) {
                binding.lineView.visibility = View.VISIBLE

                val convertedParticipants = participants?.map {
                    com.example.zdravlje.data.model.response.chat.Participant(
                        sgco_id = it.sgco_id?.toInt(),
                        sgco_name = it.sgco_name,
                        email = it.email,
                        creator = it.creator.toString()
                    )
                } ?: emptyList()

                combinedList = (convertedParticipants + calendarViewModel.selectedParticipants)
                    .distinctBy { it.sgco_id }

                println("hhhhhhhhhhhhhhhhh")
                scheduleCallAdapter = ScheduleCallAdapter(combinedList)
                binding.recyclerViewParticipiants.layoutManager =
                    LinearLayoutManager(requireContext())
                binding.recyclerViewParticipiants.adapter = scheduleCallAdapter
                scheduleCallAdapter.updateData(combinedList)

            } else {
                println("hhhhhhhhhhhhhhhhh11111111111")

                scheduledCallParticipantsAdapter = ScheduledCallParticipantsAdapter(
                    participants,
                    calendarViewModel,
                    object : ScheduledCallParticipantsAdapter.OnParticipantRemovedListener {
                        override fun onParticipantRemoved(updatedList: List<Participant>) {
                            participants = updatedList
                        }
                    }
                )
                binding.recyclerViewParticipiants.layoutManager =
                    LinearLayoutManager(requireContext())
                binding.recyclerViewParticipiants.adapter = scheduledCallParticipantsAdapter
            }

        binding.mtvSave.setOnClickListener(){
            val date = DateUtils.convertDotddMMyyyToHorLineyyyyMMdd(binding.mtvDateValue.text.toString())
            calendarViewModel.updateCall(
                EventDetailsRequest(
                    eve_start_time = date + " " + binding.mtvTimeFromValue.text.toString().trim(),
                    eve_end_time = date + " " + binding.mtvTimeToValue.text.toString().trim(),
                    pin = getStoredPin(),
                    eve_id = event?.eventId.toString(),
                    eve_title = event?.eventTitle,

                    eve_payload = EvePayload(
                        participants = participants?.map { participant ->
                            com.example.zdravlje.data.model.response.chat.Participant(
                                sgco_id = participant.sgco_id?.toInt(),
                                sgco_name = participant.sgco_name,
                                email = if (participant.email.isNullOrBlank()) null else participant.email,
                                creator = participant.creator.toString()
                            )
                        },
                        vcc_id = vccId
                    ),
                )
            )
        }
        }
    }

    companion object {
        fun newInstance(
            event: Event? = null,
            eventNotification: com.example.zdravlje.data.model.response.notification.Event? = null,
            headerTitle: String? = null,
            className: String? = null
        ): ScheduledCallInfoFragment {
            return ScheduledCallInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("event", event)
                    putSerializable("eventNotification", eventNotification)
                    putString("header_title", headerTitle)
                    putString("class_name", className)
                }
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.fragment_schedule_call_network_toast_copy_txt), text)
        clipboard.setPrimaryClip(clip)
    }

    private fun setViewModels() {
        val calendarRepository = CalendarRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val calendarViewModelFactory = CalendarViewModelFactory(
            calendarRepository, dataStoreRepository
        )

        calendarViewModel = ViewModelProvider(requireActivity(), calendarViewModelFactory).get(CalendarViewModel::class.java)

        val chatRepository = ChatRepository()
        val chatViewModelFactory = ChatViewModelFactory(chatRepository, dataStoreRepository)
        chatViewModel = ViewModelProvider(this, chatViewModelFactory)
            .get(ChatViewModel::class.java)

        val profileRepository = ProfileRepository()
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)

        val activity = requireActivity()
        if (activity is HomeActivity) {
            val homeActivity = activity as HomeActivity
            mainViewModel = homeActivity.getMainViewModel()
        } else if (activity is NextOnScheduleActivity) {
            val homeActivity = activity as NextOnScheduleActivity
            mainViewModel = homeActivity.getMainViewModel()
        }
        else if (activity is NotificationActivity) {
            val homeActivity = activity as NotificationActivity
            mainViewModel = homeActivity.getMainViewModel()
        }
    }

    override fun onChecked(isChecked: Int) {
        if (isPinSet()) {
            val deleteEvent = DeleteEvent(event?.eventId, isChecked, getStoredPin())
            calendarViewModel.deleteEvent(deleteEvent)
        }
    }

    private fun setupUIEventNotification() {
        if (eventNotification?.eveRealized == 0 && eventNotification?.isFuture() == true) {
            binding.ivDelete.visibility = View.VISIBLE
        }

        eventNotification?.let {
            val eventPayload = it.eveOnClickPayload
            binding.mtvDesc.text = it.eveTitle
            vccId = eventPayload?.vcc_id
            binding.mtvCopyLink.text = "https://hepor.nites.rs/avc/$vccId"

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val startTimeInMillis = it.eveStartTime * 1000
            val date = Date(startTimeInMillis)
            binding.mtvDateValue.text = dateFormat.format(date)
            binding.mtvTimeFromValue.text = timeFormat.format(date)
            it.eveEndTime?.let { endTime ->
                val endTimeInMillis = endTime * 1000
                val endDate = Date(endTimeInMillis)
                binding.mtvTimeToValue.text = timeFormat.format(endDate)
            }

            val participants = eventPayload?.participants

            val creators = participants?.filter { it.creator == 1 }
            val creatorNames = creators?.joinToString(", ") { it.sgco_name }
                ?: getString(R.string.fragment_scheduled_call_info_creator_call)
            binding.mtvOrganizerNameValue.text = creatorNames

            scheduledCallParticipantsAdapter = ScheduledCallParticipantsAdapter(participants, calendarViewModel)
            binding.recyclerViewParticipiants.layoutManager =
                LinearLayoutManager(requireContext())
            binding.recyclerViewParticipiants.adapter = scheduledCallParticipantsAdapter


            if (creators?.firstOrNull()?.sgco_id?.toInt() == currentUser?.sgco_id) {
                binding.mbStartCall.setOnClickListener {
                    mainViewModel.startCall(vccId?.trim().toString(), false)
                    subscribeToObservers()
                }
            } else {
                binding.mbStartCall.setOnClickListener {

                    mainViewModel.acceptCall(vccId.toString(), false)
                }
            }
        }
    }

    override fun onBottomSheetTimeFromSelected(value: String) {
        binding.mtvTimeFromValue.text = value
        if (!value.isNullOrEmpty())
            binding.mtvSave.visibility = View.VISIBLE
    }

    override fun onBottomSheetTimeToSelected(value: String) {
        binding.mtvTimeToValue.text = value
        if (!value.isNullOrEmpty())
            binding.mtvSave.visibility = View.VISIBLE
    }

    private fun subscribeToObservers() {
        chatViewModel.searchContact(SearchContact("all", limit = 10, pin = getStoredPin()))
        chatViewModel.searchContactResponseLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        dataList = responseBody
                    }
                }
            }
        )

        mainViewModel.joinCallLiveData.observe(viewLifecycleOwner) { event ->
            // Procesiranje događaja
            val context = requireContext()
            val url = event.url
            val token = event.token
            val isVideo = false
            val chatId = vccId
            val intent = DemoCallActivity.newIntent(
                context = context,
                url = url,
                token = token,
                isVideo = isVideo,
                chatId = chatId!!,
            )

            startActivity(intent)
        }

        calendarViewModel.getUpdateCallLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                       Toast.makeText(requireActivity(), " Uspesno izmenjeni podaci", Toast.LENGTH_SHORT).show()
                       requireActivity().finish()
                    }
                }
            }
        )

        calendarViewModel.getUpdateCallErrorLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                Toast.makeText(requireActivity(), "$response greskaaaa", Toast.LENGTH_SHORT).show()
            }
        )
    }
}