package com.example.zdravlje.data.model.request.login

import com.google.gson.annotations.SerializedName

data class TicketRequest(
    @SerializedName("g-recaptcha-response")
    val grecaptcharesponse: String? = "QASo0f5qEexNrw8Cvhu#jMKc!80kU80i",
    val queue: String,
    val title: String,
    @SerializedName("submitter_email")
    val submitterEmail: String,
    val description: String,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
