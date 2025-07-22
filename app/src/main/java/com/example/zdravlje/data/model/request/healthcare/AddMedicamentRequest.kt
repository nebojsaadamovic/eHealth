package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class AddMedicamentRequest(
    @SerializedName("patmed_medicine_id")
    val medicineId: Int? = null,
    @SerializedName("pin")
    val pin: String? = null,
    @SerializedName("patmed_notes")
    val patmedNotes: PatmedNotes? = null,
    @SerializedName("timezone")
    val timezone: String? = "Europe/Belgrade", //TimeZone.getDefault().id
)

data class PatmedNotes(
    @SerializedName("createDate")
    val createDate: String? = null,
    @SerializedName("arrivalDate")
    val arrivalDate: String? = null,
    @SerializedName("issuingUser")
    val issuingUser: String? = null,
    @SerializedName("pharmacist")
    val pharmacistName: String? = null,
    @SerializedName("pharmacy")
    val pharmacyName: String? = null,
    @SerializedName("dosage")
    val dosage: String? = null,
    @SerializedName("quantity")
    val quantity: String? = null,
    @SerializedName("prescriptionDiagnosis")
    val diagnosis: String? = null
)