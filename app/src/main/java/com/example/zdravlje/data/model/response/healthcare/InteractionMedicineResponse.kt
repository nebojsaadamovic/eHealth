package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class InteractionMedicineResponse(
    @SerializedName("jkl")
    val jkl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("inn")
    val inn: String,
    @SerializedName("marketing_authorization_number")
    val marketingAuthorizationNumber: String
)