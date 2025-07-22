package com.example.zdravlje.data.model.response.dashboard.event_payload

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppointmentEventPayload(
    val date: String?,
    val id: String?,
    val number: String?,
    val time: String?,
    @SerializedName("exam_type")
    val examType: String?,
    @SerializedName("doctor_name")
    val doctorName: String?,
    @SerializedName("speciality_name")
    val specialityName: String?,
    @SerializedName("institution_name")
    val institutionName: String?,
    @SerializedName("day_of_week")
    val dayOfWeek: String?,
    @SerializedName("date_created")
    val dateCreated: String?
) : Parcelable
