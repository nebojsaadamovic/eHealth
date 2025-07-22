package com.example.zdravlje.data.model.request.notification

import com.google.gson.annotations.SerializedName

data class NotificationSearchRequest(
    @SerializedName("terms")
    val terms: String? = null,
    @SerializedName("not_notification_type_id")
    val notificationTypeId: List<Int>? = null,
    @SerializedName("eve_event_type_id")
    val eventTypeId: List<Int>? = null,
    @SerializedName("sender_type")
    val senderType: Int? = null,
    @SerializedName("not_time_expected_arival_from")
    var expectedArrivalFrom: String? = null,
    @SerializedName("not_time_expected_arival_to")
    val expectedArrivalTo: String? = null,
    @SerializedName("sort")
    val sort: String? = null,
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("offset")
    val offset: Int? = null,
    @SerializedName("unread")
    val unread: Int? = null,
    @SerializedName("pin")
    val pin: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)