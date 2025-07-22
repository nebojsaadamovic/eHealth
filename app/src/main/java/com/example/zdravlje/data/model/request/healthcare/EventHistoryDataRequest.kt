package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class EventHistoryDataRequest(
    @SerializedName("eve_event_type_id")
    val eventTypeIds: List<Int>,
    @SerializedName("eve_start_time_from")
    val startTimeFrom: String,
    @SerializedName("eve_start_time_to")
    val startTimeTo: String,
    @SerializedName("limit_in")
    val limit: Int,
    @SerializedName("page_in")
    val page: Int,
    @SerializedName("pin")
    val pin: String,
    @SerializedName("sort_in")
    val sort: String,
    @SerializedName("timezone")
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
