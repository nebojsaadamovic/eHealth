package com.example.zdravlje.data.model.request

data class AnonimusLoginRequest(

    val roomid: String,
    val name: String,
    val token: String? = "df734gtuikgf983gffekw987t3igfeg",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
