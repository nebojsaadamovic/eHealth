package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class ESickRequestDTO(
    @SerializedName("urlForTempInabilityReceipt")
    val urlForTempInabilityReceipt: String? = null,
    @SerializedName("urlForTempInabilityReport")
    val urlForTempInabilityReport: String? = null,
    @SerializedName("urlForCommissionDecision")
    val urlForCommissionDecision : String? = null,
    @SerializedName("pin")
    val pin: String? = null,
    @SerializedName("sickNoteId")
    val sickNoteId: Int? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)