package com.example.zdravlje.data.model.response.dashboard.event_payload

import java.io.Serializable

data class Mandatory(
    val lek_id: Int,
    val prepisan_flag: Int,
    val doziranje: String?,
    val erx_doziranje: String?
): Serializable