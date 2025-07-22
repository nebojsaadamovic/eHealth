package com.example.zdravlje.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.notification.EventTypeEnum
import com.example.zdravlje.data.model.notification.NotificationTypeEnum
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentNotificationFilterBinding

class NotificationFilterFragment : Fragment() {

    lateinit var binding: FragmentNotificationFilterBinding
    val selectedEventTypes = mutableListOf<Int>()
    val selectedNotificationTypes = mutableListOf<Int>()
    val className = "NotificationFilterFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.mtvSave.setOnClickListener {
            val notificationFragment = NotificationFragment(
                selectedEventTypes = if (binding.showAllCheckBox.isChecked) null else selectedEventTypes,
                selectedNotificationTypes = if (binding.showAllCheckBox.isChecked) null else selectedNotificationTypes,
                dateFrom = null,
                dateTo = null,
                className = className
            )
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.notification_fragment_container_view, notificationFragment)
                .addToBackStack(null).commit()
        }

        setupCheckBoxListeners()
    }

    private fun setupCheckBoxListeners() {
        val checkBoxEventTypeMap = mapOf(
            binding.availableTherapyCheckBox to EventTypeEnum.AVAILABLE_THERAPY,
            binding.cbMedicamentReminderCheckBox to EventTypeEnum.MEDICATION_CONSUMPTION,
        )

        val checkBoxNotificationTypeMap = mapOf(
            binding.dataAccessCheckBox to NotificationTypeEnum.MEDICAL_DATA_ACCESS,
            binding.cbConfirmReceiptPrescriptionsCheckBox to NotificationTypeEnum.CONFIRMATION_RECEIPT_PRESRIPTIONS,
            binding.cbMedicamentReminderCheckBox to NotificationTypeEnum.PERSONALIZED_REMINDER,
            binding.cbNewMsg to NotificationTypeEnum.INBOX_MESSAGE,
            binding.cbAppointmentCheckBox to NotificationTypeEnum.SCHEDULED_EXAMINATION,
            binding.newMessageGroupCheckBox to NotificationTypeEnum.SOCIAL_MEDIA_MESSAGE
        )

        checkBoxEventTypeMap.forEach { (checkBox, eventTypeEnum) ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedEventTypes.add(eventTypeEnum.id)
                } else {
                    selectedEventTypes.remove(eventTypeEnum.id)
                }
            }
        }

        checkBoxNotificationTypeMap.forEach { (checkBox, notificationTypeEnum) ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedNotificationTypes.add(notificationTypeEnum.id)
                } else {
                    selectedNotificationTypes.remove(notificationTypeEnum.id)
                }
            }
        }

        binding.cbAcceptRejectedRequest.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedNotificationTypes.add(NotificationTypeEnum.REQUEST_ACCEPTED.id)
                selectedNotificationTypes.add(NotificationTypeEnum.REQUEST_REJECTED.id)
            } else {
                selectedNotificationTypes.remove(NotificationTypeEnum.REQUEST_ACCEPTED.id)
                selectedNotificationTypes.remove(NotificationTypeEnum.REQUEST_REJECTED.id)
            }
        }

        binding.cbMedicamentReminderCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedNotificationTypes.add(NotificationTypeEnum.MEDICATIONS.id)
                selectedNotificationTypes.add(NotificationTypeEnum.MISSED_MEDICATION.id)
            } else {
                selectedNotificationTypes.remove(NotificationTypeEnum.MEDICATIONS.id)
                selectedNotificationTypes.remove(NotificationTypeEnum.MISSED_MEDICATION.id)
            }
        }

        binding.sharedDataProfileCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedNotificationTypes.add(NotificationTypeEnum.SHARING_MEDICAL_DATA.id)
                selectedNotificationTypes.add(NotificationTypeEnum.REQUEST_SHARING_MEDICAL_DATA.id)
            } else {
                selectedNotificationTypes.remove(NotificationTypeEnum.SHARING_MEDICAL_DATA.id)
                selectedNotificationTypes.remove(NotificationTypeEnum.REQUEST_SHARING_MEDICAL_DATA.id)
            }
        }

        binding.showAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxEventTypeMap.keys.forEach { it.isChecked = false }
                checkBoxNotificationTypeMap.keys.forEach { it.isChecked = false }
                binding.cbAcceptRejectedRequest.isChecked = false
                binding.cbMedicamentReminderCheckBox.isChecked = false
                binding.sharedDataProfileCheckBox.isChecked = false
            }
        }

        binding.showAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedEventTypes.clear()
                selectedNotificationTypes.clear()
                checkBoxEventTypeMap.keys.forEach { it.isChecked = false }
                checkBoxNotificationTypeMap.keys.forEach { it.isChecked = false }
            }
        }
    }
}
