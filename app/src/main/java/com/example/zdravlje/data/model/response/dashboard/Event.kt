package com.example.zdravlje.data.model.response.dashboard

data class Event(
    var eve_description: String,
    var eve_event_type_id: Int,
    var eve_icon: String,
    var eve_id: Int,
    var eve_location: String,
    var eve_time: String,
    var eve_title: String
)