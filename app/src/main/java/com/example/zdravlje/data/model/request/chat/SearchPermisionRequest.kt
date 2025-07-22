package com.example.zdravlje.data.model.request.chat

import com.google.gson.annotations.SerializedName

data class SearchPermisionRequest(
    @SerializedName("sgco_id")
    var sgcoId: String? = null,
    val type: Int = 0,
    val limit: Int = 10,
    val offset: Int = 0,
    val pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
) {
    companion object {
        const val TYPE_SUBINSURED = 1
        const val TYPE_USER = 2
        const val TYPE_DOCTOR = 3
        const val TYPE_PHARMACIST = 4
    }
}
