package com.example.zdravlje.data.model.request

data class PinRequest(
    val pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
