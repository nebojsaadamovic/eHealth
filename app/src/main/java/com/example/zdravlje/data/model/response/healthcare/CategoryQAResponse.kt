package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class CategoryQAResponse(
    @SerializedName("citengcat_id")
    val catId: Int? = null,
    @SerializedName("citengcat_name_sr")
    val catNameSr: String? = null,
    @SerializedName("citengcat_name_sh")
    val catNameSh: String? = null,
    @SerializedName("citengcat_name_en")
    val catNameEn: String? = null,
    @SerializedName("citengcat_status")
    val catStatus: Int? = null,
)