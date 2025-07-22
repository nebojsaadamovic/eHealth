package com.example.zdravlje.data.model.request.calendar

data class RealizeEventRequest(
    val eve_id: Int?,
    val eve_realized: Int?,
    val pin: String,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
