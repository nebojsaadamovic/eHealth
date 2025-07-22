package com.example.zdravlje.data.model.response.notification

data class Data(
    var nk_id: Int?,
    var nk_message: String?,
    var nk_payload: String?,
    var nk_sgco_receiver_id: Int?,
    var nk_sgco_sender_id: Int?,
    var nk_status: Int?,
    var nk_time_completed: Int?,
    var nk_time_sent: Int?,
    var nk_title: String?,
    var nk_type: Int?
)