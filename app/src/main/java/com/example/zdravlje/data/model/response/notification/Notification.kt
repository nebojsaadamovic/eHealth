package com.example.zdravlje.data.model.response.notification

import com.google.gson.annotations.SerializedName

data class Notification(
    var `data`: Data? = null,
    var errors: Boolean? = null,
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("ufnc_id")
    val ufncId: Int? = null,
    @SerializedName("ufnc_user_id")
    val ufncUserId: Int? = null,
    @SerializedName("ufnc_config")
    val ufncConfig: List<String>? = null,
    @SerializedName("pin")
    val pin: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)