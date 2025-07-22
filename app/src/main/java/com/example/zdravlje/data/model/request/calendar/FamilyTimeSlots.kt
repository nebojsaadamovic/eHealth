package com.example.zdravlje.data.model.request.calendar

data class FamilyTimeSlots(
    var doctor_id: String? = "",
    var pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
