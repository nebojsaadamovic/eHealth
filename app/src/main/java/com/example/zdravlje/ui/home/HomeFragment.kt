package com.example.zdravlje.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.model.response.dashboard.event_payload.AppointmentEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.CaseEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.TherapyEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.BottomSheetEventInfoFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetRealizedUnrealizedActivityFragment
import com.example.zdravlje.ui.home.dashboard.ActivitiesFragment
import com.example.zdravlje.ui.home.dashboard.EventsFragment
import com.example.zdravlje.ui.home.dashboard.HealthFragment
import com.example.zdravlje.ui.home.dashboard.event_details.AvaliableTherapyInPharmacyActivity
import com.example.zdravlje.ui.home.dashboard.event_details.DoctorsAppointmentFragment
import com.example.zdravlje.ui.home.dashboard.event_details.DrugConsumptionActivity
import com.example.zdravlje.ui.home.dashboard.event_details.PersonalDataActivity
import com.example.zdravlje.ui.home.dashboard.event_details.RealisedTherapyInPharmacyActivity
import com.example.zdravlje.ui.home.dashboard.event_details.RealisedVisitedDoctorActivity
import com.example.zdravlje.ui.home.dashboard.event_details.ScheduledCallInfoFragment
import com.example.zdravlje.ui.interfaces.ActivityClickListener
import com.example.zdravlje.ui.notification.NotificationActivity
import com.example.zdravlje.ui.notification.NotificationViewModel
import com.example.zdravlje.ui.notification.NotificationViewModelFactory
import com.example.zdravlje.ui.profile.ProfileActivity
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.DateUtils
import com.example.zdravlje.utilities.loadImage
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment(), ActivityClickListener,
    BottomSheetRealizedUnrealizedActivityFragment.OnValueChangeListener,
    BottomSheetEventInfoFragment.OnCalendarChangeListener {

    lateinit var binding: FragmentHomeBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private val TAG = HomeFragment::class.java.simpleName

    private var lastActiveFragmentTag: String? = null
    private var userPin: String? = null
    private var userData: UserInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        observePin()
        setupObservers()
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.white_bg)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white_bg)

        if (lastActiveFragmentTag == null) {
            lastActiveFragmentTag = ActivitiesFragment::class.java.simpleName
            val fragment = ActivitiesFragment.newInstance()
            fragment.setClickListener(this)
            replaceFragment(fragment, ActivitiesFragment::class.java.simpleName)
        }

        setupUi()
    }

    private fun showSharedAvatar() {
        val sharedPrefs = requireContext().getSharedPreferences("shared_user", Context.MODE_PRIVATE)

        val sharedUserId = sharedPrefs.getString("shared_user_id", "") ?: ""
        val sharedUserPin = sharedPrefs.getString("shared_user_pin", "") ?: ""
        val sharedFirstName = sharedPrefs.getString("shared_user_first_name", "") ?: ""
        val sharedLastName = sharedPrefs.getString("shared_user_last_name", "") ?: ""
        val sharedAvatarBase64 = sharedPrefs.getString("shared_user_user_image", "") ?: ""
        val sharedUserGender = sharedPrefs.getString("shared_user_gender", "") ?: ""
        val sharedToken1 = sharedPrefs.getString("shared_token", "") ?: ""

        if (!sharedUserId.isNullOrEmpty() && (userData?.sgco_id != sharedUserId.toIntOrNull()) ) {
            binding.mcvImageWrapperSecond.visibility = View.VISIBLE

        }

        binding.mcvImageWrapperSecond.setOnClickListener() {
            binding.mcvImageWrapperSecond.visibility = View.GONE
            saveToken(sharedToken1)
            RetrofitInstance.AUTH_TOKEN = sharedToken1

            val sharedPrefs = context?.getSharedPreferences("shared_user", Context.MODE_PRIVATE)
            sharedPrefs?.edit()?.apply {
                putString("shared_user_id", "")
                apply()
            }

            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack(
                null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            // CommonUtils.openFragmentHome(requireActivity(), HomeFragment())
        }

        if (sharedAvatarBase64 != null) {
            binding.ivUserImageSecond.loadImage(
                sharedAvatarBase64,
                R.drawable.ic_contact_avatar
            )
        } else {
            val fullName = sharedFirstName + " " + sharedLastName
            binding.ivUserImageSecond.visibility = View.GONE
            binding.tvAvatarInitialsSecond.visibility = View.VISIBLE
            binding.tvAvatarInitialsSecond.text = CommonUtils.getInitials(fullName)

            if (sharedUserGender.toIntOrNull() == 2) {
                binding.tvAvatarInitialsSecond.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.screeningColor)
                )
            } else {
                binding.tvAvatarInitialsSecond.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                )
            }
        }
    }

    private fun observePin() {
        userPin = getStoredPin()
        if (view != null) {
            notificationCount()
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

    private fun setUpViewModels() {
        dataStoreRepository = DataStoreRepository(requireActivity())
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

    private fun setupUi() {
        binding.ivEvent.setOnClickListener { onImageClick(it) }
        binding.ivWayOfLife.setOnClickListener { onImageClick(it) }
        binding.ivActivity.setOnClickListener { onImageClick(it) }
        showProfileActivity()
        showNotificationActivity()
    }

    private fun onImageClick(clickedView: View) {
        binding.ivEvent.setImageResource(R.drawable.ic_events_default)
        binding.ivWayOfLife.setImageResource(R.drawable.ic_life_way_default)
        binding.ivActivity.setImageResource(R.drawable.ic_activities_default)

        when (clickedView.id) {
            R.id.ivEvent -> {
                binding.ivEvent.setImageResource(R.drawable.ic_events_selected)
                val fragment = ActivitiesFragment.newInstance()
                fragment.setClickListener(this)
                replaceFragment(fragment, ActivitiesFragment::class.java.simpleName)
            }

            R.id.ivWayOfLife -> {
                binding.ivWayOfLife.setImageResource(R.drawable.ic_life_way_selected)
                replaceFragment(HealthFragment(), HealthFragment::class.java.simpleName)
            }

            R.id.ivActivity -> {
                binding.ivActivity.setImageResource(R.drawable.ic_activities_selected)
                replaceFragment(EventsFragment(this), EventsFragment::class.java.simpleName)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        lastActiveFragmentTag = tag
        childFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

    override fun onActivityClicked(event: Event) {
        val gson = Gson()
        val eveOnClickPayload = event.eventOnClickPayload

        when (event.eventTypeId) {
            1 -> {
                if (event.eventRealized == 0 && event.isPastTime()) {
                    val bottomSheet = BottomSheetEventInfoFragment(event)
                    bottomSheet.setCalendarChangeListener(this)
                    bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                } else {
                    val medicineEventPayload =
                        gson.fromJson(eveOnClickPayload, MedicineEventPayload::class.java)
                    val intent = Intent(requireActivity(), DrugConsumptionActivity::class.java)
                    intent.putExtra("medicineEventPayload", medicineEventPayload)
                    intent.putExtra("event", event)
                    intent.putExtra("class_name", "HomeFragment")
                    startActivity(intent)
                }
            }

            2 -> {
                val appointmentEventPayload =
                    gson.fromJson(eveOnClickPayload, AppointmentEventPayload::class.java)
                val bundle = Bundle().apply {
                    putParcelable("doctors_appointment_details", appointmentEventPayload)
                }

                val fragment = DoctorsAppointmentFragment()
                startNewFragment(fragment)
                fragment.arguments = bundle
                //   }
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
                val keyHeaderTitle =
                    getString(R.string.fragment_calendar_appointment_examination_personal_activity)
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
                    val intent = Intent(requireActivity(), PersonalDataActivity::class.java).apply {
                        putExtra("className", "HomeFragment")
                    }
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

            7 -> {
                val title = getString(R.string.fragment_calendar_appointment_examination_info_table)
                val className = "HomeFragment"
                val fragment = ScheduledCallInfoFragment.newInstance(event, null, title, className)
                startNewFragment(fragment)
            }
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

    private fun showProfileActivity() {
        binding.imageWrapper.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        })
    }

    private fun showNotificationActivity() {
        binding.ivNotification.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
        })
    }

    @SuppressLint("SetTextI18n")
    fun setupObservers() {
        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
           userData = userInfo.body()
            showSharedAvatar()

            val avatarValue = userData?.avt_encoded_value
            val fullName = userData?.sgco_name ?: ""

            val userNameValue =
                userData?.pat_first_name + " " + userData?.pat_last_name?.get(0) + "."
            binding.mtvUserNameValue.text = userNameValue

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

    override fun onValueChange(eventId: Int, eventType: Int) {
        if (lastActiveFragmentTag == ActivitiesFragment::class.java.simpleName) {
        }
        val activitiesFragment =
            childFragmentManager.findFragmentByTag(ActivitiesFragment::class.java.simpleName) as? ActivitiesFragment
        if (activitiesFragment?.isVisible == true) {
            activitiesFragment.refreshData()
        }
    }

    override fun onCalendarChange(eventId: Int, eventType: Int) {
        if (lastActiveFragmentTag == ActivitiesFragment::class.java.simpleName) {
        }
        val activitiesFragment =
            childFragmentManager.findFragmentByTag(ActivitiesFragment::class.java.simpleName) as? ActivitiesFragment
        if (activitiesFragment?.isVisible == true) {
            activitiesFragment.refreshData()
        }
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getUserInformation()
    }
}