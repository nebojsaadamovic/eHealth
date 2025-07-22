package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class InstitutionsArrayResponse(
    @SerializedName("ssn")
    val ssn: String? = null,
    @SerializedName("npi")
    val npi: String? = null,
    @SerializedName("lastDateUpdate")
    val lastDateUpdate: String? = null,
    @SerializedName("institutions")
    val institutions: List<Institution>,
    @SerializedName("institutionCodes")
    val institutionCodes: List<String>? = null,
)

data class Institution(
    @SerializedName("institutionCode")
    val institutionCode: String? = null,
    @SerializedName("lastUpdate")
    val lastUpdate: String? = null,
    @SerializedName("institutionName")
    val institutionName: String? = null,
    @SerializedName("citenginst_registration_number")
    val registrationNumber: String? = null,
    @SerializedName("citenginst_name")
    val name: String? = null
)