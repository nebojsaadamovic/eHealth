package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class DeleteMedicamenteRequest(
    @SerializedName("patmed_id")
    val patmedId: String? = null,
    val pin: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)

data class PatmedDataRequest(
    val patmed_id: Int? = null,
    val patmed_medicine_id: Int? = null,
    val patmed_notes: PatmedNotesRequest? = null,
    val pin: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)

data class PatmedNotesRequest(
    val createDate: String? = null,
    val arrivalDate: String? = null,
    val issuingUser: String? = null,
    val pharmacy: String? = null,
    val pharmacist: String? = null,
    val dosage: String? = null,
    val prescriptionDiagnosis: String? = null,
    val quantity: String? = null,
    val pin: String? = null,
    val timezone: String? = null
)


