package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class MedicalCartonResponse(
    @SerializedName("caseDate")
    val caseDate: String? = null,
    @SerializedName("caseInstitution")
    val caseInstitution: String? = null,
    @SerializedName("institutionCode")
    val institutionCode: String? = null,
    @SerializedName("detail")
    val detail: MedicalCartonDetailResponse? = null
)
