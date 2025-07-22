package com.example.zdravlje.data.model.response.calendar

import com.google.gson.annotations.SerializedName

data class ScheduleResponse(
    @SerializedName("day")
    val day: String,
    @SerializedName("day_available")
    val dayAvailable: Boolean,
    @SerializedName("timeslots")
    val timeslots: Timeslots
)

data class Timeslots(
    @SerializedName("timeslot")
    val timeslot: List<Timeslot>
)

data class Timeslot(
    @SerializedName("id")
    val id: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("available")
    val available: Boolean,
    @SerializedName("note")
    val note: List<String>
)

data class TimeslotRequest(
    @SerializedName("timeslot_id")
    val timeslotId: String,
    val pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)

