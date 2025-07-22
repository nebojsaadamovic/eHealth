package com.example.zdravlje.data.model.request

data class UpdateMyProfileRequest(
    var pin: String? = null,
    var pat_short_name: String? = null,
    var pat_address: String? = null,
    var pat_email: String? = null,
    var pat_phone_mobile: String? = null,
    var pat_phone_other: String? = null,
    var pat_blood_type: String? = null,
    var us_password: String? = null,
    var us_language_id: Int? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)

