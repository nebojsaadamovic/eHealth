package com.example.zdravlje.ui.healthcare

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.healthcare.ScreeningExaminationsData
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.appointment_scheduling.AppointmentSchedulingFragment
import com.example.zdravlje.ui.healthcare.esickleave.ESickLeaveFragment
import com.example.zdravlje.ui.healthcare.medicalcarton.MedicalCartonFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.PrescribedListMedicamentsFragment
import com.example.zdravlje.ui.healthcare.prevention.PreventionFragment
import com.example.zdravlje.ui.healthcare.screeningexaminations.ScreeningExaminationsFragment
import com.example.zdravlje.ui.notification.NotificationActivity
import com.example.zdravlje.ui.notification.NotificationViewModel
import com.example.zdravlje.ui.notification.NotificationViewModelFactory
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.DateUtils
import com.example.zdravlje.utilities.loadImage
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentHealthcareBinding

class HealthcareFragment : BaseFragment() {

    private lateinit var binding: FragmentHealthcareBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var notificationViewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHealthcareBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        subscribeToObservers()
        setUpUI()
    }

    private fun setUpViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory)
            .get(HealthcareViewModel::class.java)

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

    private fun subscribeToObservers() {
        notificationCount()

        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            val userData = userInfo.body()

            val avatarValue = userData?.avt_encoded_value
            val fullName = userData?.sgco_name ?: ""

            if (!avatarValue.isNullOrEmpty()) {
                binding.ivAvatar.visibility = View.VISIBLE
                binding.tvAvatarInitials.visibility = View.GONE
                binding.ivAvatar.loadImage(
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

                binding.ivAvatar.visibility = View.GONE
                binding.tvAvatarInitials.visibility = View.VISIBLE
                binding.tvAvatarInitials.text = CommonUtils.getInitials(fullName)
            }
        }
    }

    private fun notificationCount() {
        notificationViewModel.notificationCount(
            NotificationReadRequest(
                pin = getStoredPin(),
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

    private fun setUpUI() {
        binding.clCarton.setOnClickListener {
            CommonUtils.openFragmentHome(
                requireActivity(),
                MedicalCartonFragment(null)
            )
        }

        binding.clOLL.setOnClickListener {
            CommonUtils.openFragmentHome(requireActivity(), PrescribedListMedicamentsFragment())
        }

        binding.clESickLeave.setOnClickListener {
            CommonUtils.openFragmentHome(requireActivity(), ESickLeaveFragment())
        }

        binding.clScreeningExaminations.setOnClickListener {
            openScreeningExaminationsFragment()
        }

        binding.ivNotification.setOnClickListener {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }

        binding.clPrevention.setOnClickListener() {
            CommonUtils.openFragmentHome(requireActivity(), PreventionFragment())
        }

        binding.clAppointmentScheduling.setOnClickListener {
            CommonUtils.openFragmentHome(
                requireActivity(),
                AppointmentSchedulingFragment(className = "HealthcareFragment")
            )
        }
    }

    private fun openScreeningExaminationsFragment() {
        val screeningExaminationsArr = arrayOf(
            ScreeningExaminationsData(
                "01-11-2024",
                "08:30",
                "hard code - Klinika za internu medicinu",
                "Završeno",
                "",
                "",
                "",
                ""
            ),
            ScreeningExaminationsData(
                "15-11-2024",
                "10:00",
                "hard code - Centar za dijagnostiku",
                "Zakazano",
                "",
                "",
                "",
                ""
            ),
            ScreeningExaminationsData(
                "20-11-2024",
                "09:15",
                "hard code - Medicinski centar",
                "U toku",
                "",
                "",
                "",
                ""
            ),
            ScreeningExaminationsData(
                "30-11-2024",
                "12:00",
                "hard code - Bolnica za opštu medicinu",
                "Otkaženo",
                "",
                "",
                "",
                ""
            ),
            ScreeningExaminationsData(
                "10-12-2024",
                "14:45",
                "hard code - Specijalistički centar",
                "Završeno",
                "",
                "",
                "",
                ""
            )
        )
        CommonUtils.openFragmentHome(
            requireActivity(),
            ScreeningExaminationsFragment(screeningExaminationsArr)
        )
    }
}
