package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class MedicalCartonDetailResponse(
    @SerializedName("admissionCabinetName")
    val admissionCabinetName: String? = null,
    @SerializedName("admissionDate")
    val admissionDate: String? = null,
    @SerializedName("admissionDoctor")
    val admissionDoctor: String? = null,
    @SerializedName("referralType")
    val referralType: String? = null,
    @SerializedName("src")
    val src: String? = null,
    @SerializedName("admissionProceduresSerialized")
    val admissionProceduresSerialized: String? = null,
    @SerializedName("referralTypeID")
    val referralTypeID: String? = null,
    @SerializedName("referralResponseTypeName")
    val referralResponseTypeName: String? = null,
    @SerializedName("referralResponseStatusName")
    val referralResponseStatusName: String? = null,
    @SerializedName("referralResponseDate")
    val referralResponseDate: String? = null,
    @SerializedName("report")
    val report: Boolean? = null,
    @SerializedName("id")
    val id: String? = null
)
