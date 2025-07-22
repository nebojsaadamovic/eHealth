package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class CommonsThemesRequest(
    @SerializedName("terms")
    val terms: String? = null,
    @SerializedName("sg_id")
    val sgId: Int? = null,
    @SerializedName("sgm_id")
    val sgmId: Int? = null,
    @SerializedName("limit")
    val limit: Int = 10,
    @SerializedName("offset")
    val offset: Int = 0,
    @SerializedName("like")
    val like: Int? = null,
    @SerializedName("pin")
    val pin: String? = null,
    @SerializedName("follow")
    val follow: Int? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
