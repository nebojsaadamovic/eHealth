package com.example.zdravlje.data.model.request.calendar

class FamilyTimeSlotsForTelemedicine(
    var doctor_id: String? = "",
    var pin: String? = "",
    var from: String? = "",
    var to: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)