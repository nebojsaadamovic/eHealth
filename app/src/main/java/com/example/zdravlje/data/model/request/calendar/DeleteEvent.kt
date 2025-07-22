package com.example.zdravlje.data.model.request.calendar

data class DeleteEvent(
    var eve_id: Int? = 0,
    var range: Int? = 0,
    var pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
