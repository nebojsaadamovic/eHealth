package com.example.zdravlje.data.repository.notification

import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.model.request.notification.NotificationSearchRequest
import com.example.zdravlje.data.model.request.notification.NotificationSettingsRequest
import com.example.zdravlje.data.model.response.notification.Notification
import com.example.zdravlje.data.model.response.notification.NotificationResponse
import com.example.zdravlje.data.model.response.notification.NotificationSettingsResponse
import retrofit2.Response

class NotificationRepository {

    suspend fun getNotifications(notificationSearchRequest: NotificationSearchRequest): Response<List<NotificationResponse>> {
        return RetrofitInstance.appApi.getNotifications(notificationSearchRequest)
    }

    suspend fun setNotificationRead(notificationReadRequest: NotificationReadRequest): Response<String> {
        return RetrofitInstance.appApi.setNotificationRead(notificationReadRequest)
    }

    suspend fun notificationCount(notificationReadRequest: NotificationReadRequest): Response<Notification> {
        return RetrofitInstance.appApi.notificationCount(notificationReadRequest)
    }

    suspend fun getNotificationConf(pin: PinRequest): Response<Notification> {
        return RetrofitInstance.appApi.getNotificationConf(pin)
    }

    suspend fun setNotificationConf(notification: Notification): Response<Notification> {
        return RetrofitInstance.appApi.setNotificationConf(notification)
    }

    suspend fun setNotificationSettings(notificationSettingsRequest: NotificationSettingsRequest): Response<Any> {
        return RetrofitInstance.appApi.setNotificationSettings(notificationSettingsRequest)
    }

    suspend fun getNotificationSettings(pin: PinRequest): Response<NotificationSettingsResponse> {
        return RetrofitInstance.appApi.getNotificationSettings(pin)
    }
}