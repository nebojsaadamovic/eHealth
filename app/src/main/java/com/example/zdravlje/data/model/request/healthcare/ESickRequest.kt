package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ESickRequest(
    @SerializedName("dateFrom")
    val date: String? = getDefaultDateFrom(),
    @SerializedName("pin")
    val pin: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)

fun getDefaultDateFrom(): String {
    val today = LocalDate.now()
    val dateFrom = today.minusDays(365)
    return dateFrom.format(DateTimeFormatter.ISO_DATE)
}