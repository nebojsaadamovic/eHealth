package com.example.zdravlje.data.model.request.calendar

import com.google.gson.annotations.SerializedName

data class ExamDeleteRequest(
    @SerializedName("exam_number")
    val examNumber: String? = null,
    val pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)