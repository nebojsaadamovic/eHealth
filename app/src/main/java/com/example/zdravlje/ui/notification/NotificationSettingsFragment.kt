package com.example.zdravlje.ui.notification

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.notification.NotificationSettingsRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentNotificationSettingsBinding

class NotificationSettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentNotificationSettingsBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    private var emailNotificationValue = 0
    private var pushNotificationValue = 0
    private var isUserAction = false
    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        getDataFromServer(triggeredByUserAction = false)
        setupClickListeners()
        subscribeObservers()
    }

    private fun getDataFromServer(triggeredByUserAction: Boolean) {
        if (!triggeredByUserAction) {
            isUserAction = false
        }

        notificationViewModel.getNotificationSettings(PinRequest(pin = getStoredPin()))
        notificationViewModel.getNotificationSettingsResponseLiveData.observe(
            viewLifecycleOwner
        ) { response ->
            if (response != null && response.isSuccessful) {
                response.body()?.let { data ->
                    emailNotificationValue = data.email_notification
                    pushNotificationValue = data.push_notification
                    isDataLoaded = true
                    setupUi()
                }
            }
        }
    }

    private fun setupUi() {
        if (!isAdded || !isDataLoaded) return
        binding.scEmailNotification.setOnCheckedChangeListener(null)
        binding.scEmailNotification.isChecked = (emailNotificationValue == 1)
        binding.scEmailNotification.setOnCheckedChangeListener { _, isChecked ->
            isUserAction = true
            emailNotificationValue = if (isChecked) 1 else 0

            notificationViewModel.setNotificationSettings(
                NotificationSettingsRequest(
                    pin = getStoredPin(),
                    email_notification = emailNotificationValue,
                    push_notification = pushNotificationValue
                )
            )
        }

        val areNotificationsEnabled = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        binding.clSettings.visibility = if (!areNotificationsEnabled) View.VISIBLE else View.GONE
        binding.scConfirmRecipe.isChecked = areNotificationsEnabled
    }

    private fun setupClickListeners() {
        binding.mbEnableNotifications.setOnClickListener {
            val intent = Intent().apply {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            }
            startActivity(intent)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun subscribeObservers() {
        notificationViewModel.getNotificationSettingsLiveData.observe(viewLifecycleOwner) { response ->
            if (response != null && response.isSuccessful) {
                response.body()?.let {
                    if (isUserAction) {
                        showToastForSettings()
                        openFragment(NotificationFragment())
                        isUserAction = false
                    }
                }
            }
        }

        notificationViewModel.getNotificationSettingsErrorLiveData.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireActivity(), "${getString(R.string.error)} $error", Toast.LENGTH_SHORT).show()
            isUserAction = false
        }
    }

    private fun setUpViewModels() {
        val notificationRepository = NotificationRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val factory = NotificationViewModelFactory(notificationRepository, dataStoreRepository)
        notificationViewModel = ViewModelProvider(requireActivity(), factory)
            .get(NotificationViewModel::class.java)
    }

    private fun openFragment(nameFragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.notification_fragment_container_view, nameFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showToastForSettings() {
        val message = if (emailNotificationValue == 1) {
            getString(R.string.fragment_notification_settings_email_notifications_enabled)
        } else {
            getString(R.string.fragment_notification_settings_email_notifications_disabled)
        }
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        getDataFromServer(triggeredByUserAction = false)
    }
}
