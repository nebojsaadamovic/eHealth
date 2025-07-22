package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("exam_type")
    val examType: String,
    @SerializedName("doctor_name")
    val doctorName: String,
    @SerializedName("speciality_name")
    val specialityName: String,
    @SerializedName("institution_name")
    val institutionName: String,
    @SerializedName("day_of_week")
    val dayOfWeek: String,
    @SerializedName("date_created")
    val dateCreated: String,
    @SerializedName("app_type")
    val appType: String,
    @SerializedName("app_type_text")
    val appTypeText: String
)