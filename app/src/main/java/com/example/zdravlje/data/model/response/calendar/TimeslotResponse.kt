package com.example.zdravlje.data.model.response.calendar

import com.google.gson.annotations.SerializedName

data class TimeslotResponse(
    @SerializedName("exam_number")
    val examNumber: String
)