package com.example.zdravlje.data.model.response.dashboard.future

data class DailyEvents(
    var eventDate: String,
    var events: List<Event>
)