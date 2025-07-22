package com.example.zdravlje.data.model.request.profile

import com.google.gson.annotations.SerializedName

data class SetPermission(
    @SerializedName("sgco_id")
    var sgcoId: String? = "0",
    @SerializedName("shared_data")
    var sharedData: List<SharedDataRequestModel>,
    @SerializedName("shared_notes")
    var sharedNotes: String? = "",
    @SerializedName("pin")
    var pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
