package com.example.zdravlje.data.model.response.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class EidTokenResponse(
    @SerializedName("data")
    @Expose
    var data: String? = null,

    @SerializedName("errors")
    @Expose
    var errors: Boolean? = null
)