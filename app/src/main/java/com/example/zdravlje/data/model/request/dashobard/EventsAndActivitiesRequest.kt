package com.example.zdravlje.data.model.request.dashobard

data class EventsAndActivitiesRequest(
    val eve_start_time_from: String,
    val eve_start_time_to: String,
    val eve_event_type_id: Int? = null,
    val sort_in: String? = null,
    val limit_in: Int? = null,
    val offset_in: Int? = 0,
    val mobile_group: Int? = 1,
    var pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)