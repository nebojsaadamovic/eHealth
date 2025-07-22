package com.example.zdravlje.data.model.request.notification

import com.google.gson.annotations.SerializedName

data class NotificationReadRequest(
    @SerializedName("not_ids")
    val notIds: List<Int>? = null,
    @SerializedName("pin")
    val pin: String? = null,
    @SerializedName("not_time_expected_arival_to")
    val currentTime: String? = null,
    @SerializedName("unread")
    val unread: Int? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)