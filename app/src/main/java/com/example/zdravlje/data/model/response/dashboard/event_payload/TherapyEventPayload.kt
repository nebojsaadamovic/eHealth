package com.example.zdravlje.data.model.response.dashboard.event_payload

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class TherapyEventPayload(
    val details: String,
    val date: String,
    val remainder: String,
    val place: String,
    val drugCode: String,
    val chronicTherapyID: Int?,
    val prescriptionTypeName: String,
    val prescriptionStatus: TherapyPrescriptionStatus
): Serializable

@Parcelize
data class TherapyEventPayloadParcelable(
    val details: String,
    val date: String,
    val remainder: String,
    val place: String,
    val drugCode: String,
    val chronicTherapyID: Int?,
    val prescriptionTypeName: String,
    val prescriptionStatus: TherapyPrescriptionStatus
): Parcelable