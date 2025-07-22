package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class MedicalCartonRequest(
    @SerializedName("contact_id")
    val contactId: Int? = null,
    @SerializedName("pin")
    val pin: String? = null,
    @SerializedName("pageNumber")
    val pageNumber: Int? = null,
    @SerializedName("caseDate")
    val caseDate: String? = null,
    @SerializedName("sortBy")
    val sortBy: String? = null,
    @SerializedName("institutionCode")
    val institutionCode: String? = "",
    @SerializedName("code")
    val institutionId: String? = null,
    @SerializedName("dateFrom")
    val dateFrom: String? = null,
    @SerializedName("dateTo")
    val dateTo: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
