package com.example.zdravlje.data.model.request.chat

import android.os.Parcelable
import com.example.zdravlje.data.model.response.chat.Participant
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class EventDetailsRequest(
    @SerializedName("g-recaptcha-response")
    val grecaptcharesponse: String? = "QASo0f5qEexNrw8Cvhu#jMKc!80kU80i",
    val eve_title: String? = null,
    val eve_start_time: String? = null,
    val eve_end_time: String? = null,
    var pin: String? = "",
    val timezone: String? = "Europe/Belgrade", //TimeZone.getDefault().id
    val eve_payload: EvePayload? = null,
    val eve_id: String? = null
)

@Parcelize
data class EvePayload(
    val participants: List<Participant>? = null,
    val vcc_id: String? = null
) : Parcelable