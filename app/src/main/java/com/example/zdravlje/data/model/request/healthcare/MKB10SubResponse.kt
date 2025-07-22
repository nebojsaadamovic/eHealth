package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class MKB10SubResponse(
    @SerializedName("cbicdsg_id")
    val cbicdsgId: Int? = null,
    @SerializedName("cbicdsg_code")
    val cbicdsgCode: String? = null,
    @SerializedName("cbicdsg_name_sr")
    val cbicdsgNameSr: String? = null,
    @SerializedName("cbicdsg_name_sh")
    val cbicdsgNameSh: String? = null,
    @SerializedName("cbicdsg_name_en")
    val cbicdsgNameEn: String? = null,
    @SerializedName("cbicdsg_name_ru")
    val cbicdsgNameRu: String? = null,
    @SerializedName("cbicdsg_icd10group_id")
    val cbicdsgIcd10GroupId: Int? = null,
    @SerializedName("cbicdsg_status")
    val cbicdsgStatus: Int? = null
)
