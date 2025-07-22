package com.example.zdravlje.data.model.request.chat

data class SendContactRequest(
    val pin: String? = null,
    var sgco_id: Int,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)


data class SendContactListRequest(
    val pin: String? = null,
    var sgco_id: List<Int>,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)