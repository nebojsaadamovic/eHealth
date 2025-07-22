package com.example.zdravlje.ui.chat.scheduleCall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.chat.ScheduleCallData
import com.example.zdravlje.data.model.request.chat.EvePayload
import com.example.zdravlje.data.model.request.chat.EventDetailsRequest
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.ChatRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.schedulecall.ScheduleCallAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.BottomSheetInsertValue
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.ChatViewModelFactory
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.DateUtils.showDatePickerDialog
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentScheduleCallBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleCallFragment : BaseFragment(), BottomSheetTimeToFragment.OnBottomSheetTimeToListener,
    BottomSheetTimeFromFragment.OnBottomSheetTimeFromListener,
    BottomSheetInsertValue.OnValueListener {

    lateinit var binding: FragmentScheduleCallBinding
    lateinit var scheduleCallAdapter: ScheduleCallAdapter
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var userPin: String? = null
    var userData: UserInfo? = null
    private var dataList: List<Participant> = emptyList()
    private var selectedParticipants: List<Participant> = emptyList()
    private var dataScheduleCallParcelable: ScheduleCallData? = null
    private var addedParticipants: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleCallBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        observePin()

        selectedParticipants =
            arguments?.getParcelableArrayList("selectedParticipants") ?: emptyList()
        dataScheduleCallParcelable = arguments?.getParcelable<ScheduleCallData>("scheduleCallData")
        addedParticipants = arguments?.getString("addedParticipants")

        dataScheduleCallParcelable?.let { scheduleCallData ->
            binding.mtvDescValue.text = scheduleCallData.desc ?: ""
            binding.mtvDateValue.text = scheduleCallData.date ?: ""
            binding.mtvTimeFromValue.text = scheduleCallData.timeFrom ?: ""
            binding.mtvTimeToValue.text = scheduleCallData.timeTo ?: ""
            binding.mtvAddParticipiant.text = addedParticipants
        }

        setupUI()
    }

    private fun setUpViewModels() {
        val chatRepository = ChatRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val chatViewModelFactory = ChatViewModelFactory(chatRepository, dataStoreRepository)
        chatViewModel = ViewModelProvider(this, chatViewModelFactory)
            .get(ChatViewModel::class.java)

        val calendarRepository = CalendarRepository()
        val calendarViewModelFactory =
            CalendarViewModelFactory(calendarRepository, dataStoreRepository)

        calendarViewModel = ViewModelProvider(
            requireActivity(),
            calendarViewModelFactory
        ).get(CalendarViewModel::class.java)
        val profileRepository = ProfileRepository()
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)
    }

    private fun subscribeToObservers() {
        val searchContact = SearchContact("all", limit = 10, pin = userPin)
        chatViewModel.searchContact(searchContact)
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

        val msgToast = getString(R.string.fragment_schedule_call_choose_toast_msg_successfully)
        calendarViewModel.getCreateCallLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {

                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(
                            requireActivity(),
                            msgToast,
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                    }
                }
            }
        )

        calendarViewModel.getCreateCallErrorLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Toast.makeText(
                            requireActivity(),
                            "${getString(R.string.error)}: ${responseBody}",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                    }
                }
            }
        )
    }

    private fun observePin() {

        userPin=getStoredPin()
        subscribeToObservers()

        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            userData = userInfo.body()
        }
    }

    private fun setupUI() {
        if (selectedParticipants.isNotEmpty()) {
            binding.lineView.visibility = View.VISIBLE
            scheduleCallAdapter = ScheduleCallAdapter(selectedParticipants)
            binding.recyclerViewParticipants.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewParticipants.adapter = scheduleCallAdapter
            scheduleCallAdapter.updateData(selectedParticipants)
        }

        binding.mtvDateValue.setOnClickListener(View.OnClickListener {
            showDatePickerDialog(requireContext(), binding.mtvDateValue) {
            }
        })

        val chooseDescription = getString(R.string.fragment_schedule_call_choose_description)
        val insertDescription = getString(R.string.fragment_schedule_call_choose_insert_description)
        binding.mtvDescValue.setOnClickListener(View.OnClickListener {
            val bottomSheetInsertValue = BottomSheetInsertValue().apply {
                arguments = Bundle().apply {
                    putString("title", chooseDescription)
                    putString("subtitle", insertDescription)
                }
            }
            bottomSheetInsertValue.setOnValueListener(this)
            bottomSheetInsertValue.show(
                requireActivity().supportFragmentManager,
                bottomSheetInsertValue.tag
            )
        })

        binding.mtvTimeFromValue.setOnClickListener(View.OnClickListener {
            val bottomSheetTimeFromFragment = BottomSheetTimeFromFragment()
            bottomSheetTimeFromFragment.setListener(this)
            bottomSheetTimeFromFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetTimeFromFragment.tag
            )
        })

        binding.mtvTimeToValue.setOnClickListener(View.OnClickListener {
            val bottomSheetTimeToFragment = BottomSheetTimeToFragment()
            bottomSheetTimeToFragment.setTargetFragment(this, 0)
            bottomSheetTimeToFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetTimeToFragment.tag
            )
        })

        binding.mtvCreateCall.setOnClickListener {
            val inputDate = dataScheduleCallParcelable?.date.toString()
            val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val parsedDate = LocalDate.parse(inputDate, inputFormatter)
            val formattedDate = parsedDate.format(outputFormatter)

            val isCreator = "1"
            val creator = userData?.let { obj ->

                Participant(
                    sgco_id = obj.sgco_id,
                    sgco_name = obj.sgco_name,
                    email = if (obj.us_email.isNullOrBlank()) null else obj.us_email,
                    creator = isCreator
                )
            }

            val isNotCreator = "0"
            val eventDetailsRequest = EventDetailsRequest(
                eve_start_time = formattedDate + " " + dataScheduleCallParcelable?.timeFrom.toString(),
                eve_end_time = formattedDate + " " + dataScheduleCallParcelable?.timeTo.toString(),
                eve_title = dataScheduleCallParcelable?.desc.toString(),
                eve_payload = EvePayload(
                    participants = selectedParticipants.map { participant ->
                        Participant(
                            sgco_id = participant.sgco_id,
                            sgco_name = participant.sgco_name,
                            email =  if (participant.email.isNullOrBlank()) null else participant.email,
                            creator = isNotCreator
                        )
                    } + creator!!
                ),
                pin = getStoredPin()
            )
            calendarViewModel.createCall(eventDetailsRequest)
        }

        binding.mtvAddParticipiantValue.setOnClickListener() {
            val scheduleCallData = ScheduleCallData(
                binding.mtvDescValue.text.toString(),
                binding.mtvOrganizerValue.text.toString(),
                binding.mtvDateValue.text.toString(),
                binding.mtvTimeFromValue.text.toString(),
                binding.mtvTimeToValue.text.toString()
            )
            val bundle = Bundle().apply {
                putParcelable("scheduleCallData", scheduleCallData)
            }

            if (selectedParticipants.isNotEmpty()){
                dataList = selectedParticipants
            }
            val fragment = ScheduleCallParticipiantFragment(dataList, selectedParticipants = selectedParticipants).apply {
                arguments = bundle
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.chatOptionFcv, fragment).addToBackStack(null).commit()
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onBottomSheetTimeFromSelected(value: String) {
        binding.mtvTimeFromValue.text = value
        Toast.makeText(
            requireContext(),
            "${getString(R.string.fragment_schedule_call_start_time)}: $value",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onBottomSheetTimeToSelected(value: String) {
        binding.mtvTimeToValue.text = value
        Toast.makeText(
            requireContext(),
            "${getString(R.string.fragment_schedule_call_end_time)}: $value",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onValueChange(value: String) {
        binding.mtvDescValue.text = value
    }
}