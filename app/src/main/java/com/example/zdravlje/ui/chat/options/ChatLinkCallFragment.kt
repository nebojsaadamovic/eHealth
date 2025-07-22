package com.example.zdravlje.ui.chat.options

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
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.chat.EvePayload
import com.example.zdravlje.data.model.request.chat.EventDetailsRequest
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.BottomSheetInsertValue
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChatLinkCallBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatLinkCallFragment : BaseFragment(), BottomSheetInsertValue.OnValueListener {

    private lateinit var binding: FragmentChatLinkCallBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var mainViewModel: MainViewModel

    var insertValue: String? = null
    var userData: UserInfo? = null
    var userPin: String? = null
    var vccId: String? = null
    var baseLink: String? = null
    var generateTab :Boolean= false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatLinkCallBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        subscribeToObservers()
        setupUi()
    }

    private fun setupUi() {
        binding.mtvInsertValue.setOnClickListener() {
            val title = getString(R.string.fragment_chat_link_call_title_description)
            val hint = getString(R.string.fragment_chat_link_call_hint_description)

            val bottomSheetInsertValue = BottomSheetInsertValue().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("subtitle", hint)
                }
            }

            bottomSheetInsertValue.setOnValueListener(this)
            bottomSheetInsertValue.show(
                requireActivity().supportFragmentManager,
                bottomSheetInsertValue.tag
            )
        }

        binding.mbGenerate.setOnClickListener() {
            binding.progressBar.visibility = View.VISIBLE
            if (generateTab == false) {
                val serchList = userData?.let { obj ->
                    Participant(
                        sgco_id = obj.sgco_id,
                        sgco_name = obj.sgco_name,
                        email = obj.us_email
                    )
                }

                val participantsList = serchList?.let { listOf(it) } ?: emptyList()
                val evePayload = EvePayload(participants = participantsList)

                val eventDetailsRequest = EventDetailsRequest(
                    eve_start_time = getCurrentTimeString(),
                    eve_end_time = getEndTimeString(),
                    eve_title = insertValue,
                    eve_payload = evePayload,
                    pin = getStoredPin()
                )

                calendarViewModel.createCall(eventDetailsRequest)
                generateTab = true
            } else {
                val activty = requireActivity() as ChatOptionsActivity
                activty.isVideoCall = true
                activty.callId = vccId.toString()
                mainViewModel.startCall(vccId.toString(), true)
                lifecycleScope.launch {
                    delay(50000)
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.ivBack.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }

    private fun subscribeToObservers() {
        userPin=getStoredPin()
        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            userData = userInfo.body()
        }

        val msgToast = getString(R.string.fragment_schedule_call_choose_toast_msg_link_successfully)
        calendarViewModel.getCreateCallLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireActivity(),
                        msgToast,
                        Toast.LENGTH_SHORT
                    ).show()

                    baseLink = "https://hepor.nites.rs/avc/"
                    vccId = response.body()?.eve_payload?.vcc_id ?: ""
                    binding.mtvCopyLink.text = "$baseLink$vccId"
                    binding.mcvCopyLink.visibility = View.VISIBLE
                    binding.mbGenerate.text = "Započni sastanak"
                    binding.mbGenerate.setBackgroundColor(
                        ContextCompat.getColor(requireActivity(), R.color.base_blue_color)
                    )
                    binding.mbGenerate.setTextColor(
                        ContextCompat.getColor(requireActivity(), R.color.white)
                    )
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Greška prilikom kreiranja poziva",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        calendarViewModel.getCreateCallErrorLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
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

        binding.mcvCopyLink.setOnClickListener {
            val textToCopy = binding.mtvCopyLink.text
            copyToClipboard(textToCopy.toString())
            Toast.makeText(requireContext(), getString(R.string.fragment_bottom_sheet_likes_interactions_copy_txt_msg), Toast.LENGTH_SHORT).show()
        }
    }

    fun getCurrentTimeString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.now().format(formatter)
    }

    fun getEndTimeString(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.now().plusMinutes(30).format(formatter)
    }

    private fun setUpViewModels() {
        dataStoreRepository = DataStoreRepository(requireActivity())

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

        val activity = requireActivity() as ChatOptionsActivity  //ChatMessagesActivity
        mainViewModel = activity.getMainViewModel()
    }

    override fun onValueChange(value: String) {
        binding.mtvInsertValue.text = value
        insertValue = value
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied_text", text)
        clipboard.setPrimaryClip(clip)
    }
}