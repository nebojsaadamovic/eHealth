package com.example.zdravlje.data.model.response.dashboard.event_payload

import java.io.Serializable

data class CaseDetail(
    val admissionCabinetName: String,
    val admissionDate: String,
    val admissionDoctor: String,
    val referralType: String,
    val src: String,
    val admissionProceduresSerialized: String,
    val referralTypeID: String,
    val referralResponseTypeName: String,
    val referralResponseStatusName: String,
    val referralResponseDate: String,
    val report: Boolean,
    val id: String
): Serializable