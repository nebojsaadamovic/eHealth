package com.example.zdravlje.data.model.response.healthcare

data class AccessMedicalCartonResponse(
    val institutionCode: String? = null,
    val institutionName: String ?= null,
    val rfzoDoctorID: String? = null,
    val rfzoDoctorLBO: String? = null,
    val doctorFullName: String? = null,
    val createDate: String? = null,
    val npi: String? = null
)
