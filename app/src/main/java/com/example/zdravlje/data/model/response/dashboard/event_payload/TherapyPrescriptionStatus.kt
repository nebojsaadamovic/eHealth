package com.example.zdravlje.data.model.response.dashboard.event_payload

import java.io.Serializable

data class TherapyPrescriptionStatus(
    val code: String,
    val description: String
): Serializable