package com.example.zdravlje.data.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EidTokenRequest(
    @SerializedName("mobile")
    @Expose
    var mobile: Boolean = true,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)