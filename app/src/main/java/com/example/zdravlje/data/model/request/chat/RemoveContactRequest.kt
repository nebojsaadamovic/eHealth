package com.example.zdravlje.data.model.request.chat

data class RemoveContactRequest(
    val pin: String = "",
    val sgco_id: Int,
    val timezone: String? = "Europe/Belgrade", //TimeZone.getDefault().id
    val vcc_id: String? = null
)

data class AcceptContactRequest(
    val pin: String = "",
    val sgco_id: Int,
    val timezone: String? = "Europe/Belgrade", //TimeZone.getDefault().id
    val vcc_id: String? = null
)