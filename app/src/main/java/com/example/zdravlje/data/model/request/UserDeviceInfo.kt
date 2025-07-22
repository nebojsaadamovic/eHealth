package com.example.zdravlje.data.model.request

data class UserDeviceInfo(
    val lang_code: String, // Required field, possible values: sr, sh, en
    val firebase_token: String? = null,
    val device_type: Int? = null, // Device type (2 - Android)
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)