package com.example.zdravlje.ui.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.model.request.notification.NotificationSearchRequest
import com.example.zdravlje.data.model.request.notification.NotificationSettingsRequest
import com.example.zdravlje.data.model.response.notification.Notification
import com.example.zdravlje.data.model.response.notification.NotificationResponse
import com.example.zdravlje.data.model.response.notification.NotificationSettingsResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository
import com.example.zdravlje.services.avc.SampleApplication.Companion.context
import kotlinx.coroutines.launch
import retrofit2.Response
import com.nites.ezdravljeapp.R

class NotificationViewModel(
    private val notificationRepository: NotificationRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val getNotificationsLiveData: MutableLiveData<Response<List<NotificationResponse>>?> =
        MutableLiveData()
    val getNotificationsErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val setNotificationReadLiveData: MutableLiveData<Response<String>> = MutableLiveData()
    val setNotificationReadErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val notificationCountLiveData: MutableLiveData<Response<Notification>> = MutableLiveData()
    val notificationCountErrorLiveData: MutableLiveData<String> = MutableLiveData()

    var getNotificationConfLiveData: MutableLiveData<Response<Notification>?> = MutableLiveData()
    val getNotificationConfErrorLiveData: MutableLiveData<String> = MutableLiveData()

    var getNotificationSettingsLiveData: MutableLiveData<Response<Any>?> = MutableLiveData()
    val getNotificationSettingsErrorLiveData: MutableLiveData<String> = MutableLiveData()

    var getNotificationSettingsResponseLiveData: MutableLiveData<Response<NotificationSettingsResponse>?> = MutableLiveData()
    val getNotificationSettingsResponseErrorLiveData: MutableLiveData<String> = MutableLiveData()

    fun getNotifications(notificationSearchRequest: NotificationSearchRequest) {
        viewModelScope.launch {
            try {
                getNotificationConfLiveData.value = null
                getNotificationsLiveData.value = null
                val response = notificationRepository.getNotifications(notificationSearchRequest)
                if (response.isSuccessful) {
                    getNotificationsLiveData.value = response
                } else {
                    getNotificationsErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getNotificationsErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun setNotificationRead(notificationReadRequest: NotificationReadRequest) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.setNotificationRead(notificationReadRequest)
                if (response.isSuccessful) {
                    setNotificationReadLiveData.postValue(response)
                } else {
                    setNotificationReadErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                setNotificationReadErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }


    fun notificationCount(notificationReadRequest: NotificationReadRequest) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.notificationCount(notificationReadRequest)
                if (response.isSuccessful) {
                    notificationCountLiveData.value = response
                } else {
                    notificationCountErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                notificationCountErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getNotificationConf(pin: PinRequest) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.getNotificationConf(pin)
                if (response.isSuccessful) {
                    getNotificationConfLiveData.value = response
                } else {
                    getNotificationConfErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getNotificationConfErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun setNotificationConf(notification: Notification) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.setNotificationConf(notification)
                if (response.isSuccessful) {
                    getNotificationConfLiveData.value = response
                } else {
                    getNotificationConfErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getNotificationConfErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun setNotificationSettings(notificationSettingsRequest: NotificationSettingsRequest) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.setNotificationSettings(notificationSettingsRequest)
                if (response.isSuccessful) {
                    getNotificationSettingsLiveData.value = response
                } else {
                    getNotificationSettingsErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getNotificationSettingsErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }

    fun getNotificationSettings(pin: PinRequest) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.getNotificationSettings(pin)
                if (response.isSuccessful) {
                    getNotificationSettingsResponseLiveData.value = response
                } else {
                    getNotificationSettingsResponseErrorLiveData.postValue("${context?.getString(R.string.error)}: ${response.message()}")
                }
            } catch (e: Exception) {
                getNotificationSettingsResponseErrorLiveData.postValue("${context?.getString(R.string.exception)}: ${e.message}")
            }
        }
    }
}
