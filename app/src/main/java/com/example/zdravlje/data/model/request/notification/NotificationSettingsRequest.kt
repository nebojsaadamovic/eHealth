package com.example.zdravlje.data.model.request.notification

data class NotificationSettingsRequest(
    val push_notification: Int? = null,
    val email_notification: Int? = null,
    val pin: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)