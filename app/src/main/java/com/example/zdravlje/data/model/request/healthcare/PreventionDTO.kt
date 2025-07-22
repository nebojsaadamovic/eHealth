package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class PreventionDTO(
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("pin")
    val pin: String? = null,
    @SerializedName("character")
    val character: String? = null,
    @SerializedName("cbicdsg_icd10group_id")
    val cbicdsgIcd10groupId: Int? = null,
    @SerializedName("cbicd_icd10subgroup_id")
    val cbicdsgIcd10SubGroupId: Int? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
