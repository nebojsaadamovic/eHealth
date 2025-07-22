package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class RecipeRequest(
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("limit_in")
    val limitIn: Int? = 10,
    @SerializedName("offset_in")
    val offsetIn: Int? = 0,
    @SerializedName("patmed_time_start")
    val patmedTimeStart: String? = null,
    @SerializedName("patmed_time_end")
    val patmedTimeEnd: String? = null,
    @SerializedName("pin")
    val pin: String?,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)