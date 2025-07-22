package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class MedicalCartonPdfRequest(
    @SerializedName("contact_id")
    val contactId: String? = null,
    @SerializedName("pin")
    val pin: String? = null,
    @SerializedName("id")
    val reportId: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
