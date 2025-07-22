package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class TopicQAResponse(
    @SerializedName("citengtopid")
    val topid: Int? = null,
    @SerializedName("citengtopname_sr")
    val topnameSr: String? = null,
    @SerializedName("citengtopname_sh")
    val topnameSh: String? = null,
    @SerializedName("citengtopname_en")
    val topnameEn: String? = null,
    @SerializedName("citengtopstatus")
    val topstatus: Int? = null,
    @SerializedName("citengtopcitizen_engagement_category_id")
    val citizenEngagementCategoryId: Int? = null,
    @SerializedName("citengcatid")
    val catid: Int? = null,
    @SerializedName("citengcatname_sr")
    val catnameSr: String? = null,
    @SerializedName("citengcatname_sh")
    val catnameSh: String? = null,
    @SerializedName("citengcatname_en")
    val catnameEn: String? = null,
    @SerializedName("citengcatstatus")
    val catstatus: Int? = null,
    @SerializedName("topic")
    val topic: String? = null
)
