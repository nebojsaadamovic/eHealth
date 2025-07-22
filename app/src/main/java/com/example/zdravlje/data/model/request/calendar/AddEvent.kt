package com.example.zdravlje.data.model.request.calendar

data class AddEvent(
    var eve_id: Int? = null,
    var eve_calendar_id: Int? = null,
    val eve_title: String,
    val eve_event_type_id: Int,
    val eve_on_click_payload: OnClickPayload? = null,
    val interval: Interval,
    var pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)

data class Interval(
    val from: String,
    val to: String,
    val repeat: Repeat
)

data class Repeat(
    val every: Int,
    val day: List<String>
)

data class OnClickPayload(
    val mandatory: Mandatory
)

data class Mandatory(
    val lek_id: Int,
    val prepisan_flag: Int,
    val doziranje: String,
    val erx_doziranje: String
)