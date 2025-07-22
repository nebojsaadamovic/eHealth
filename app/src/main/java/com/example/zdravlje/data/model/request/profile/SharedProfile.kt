package com.example.zdravlje.data.model.request.profile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SharedProfile(
    @SerializedName("sgco_id")
    var sgcoId: String? = "0",
    @SerializedName("pin")
    var pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
) : Serializable


