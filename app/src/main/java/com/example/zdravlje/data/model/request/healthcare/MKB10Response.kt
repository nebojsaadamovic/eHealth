package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class MKB10Response(
    @SerializedName("cbicdg_id")
    val cbicdgId: Int? = null,
    @SerializedName("cbicdg_code")
    val cbicdgCode: String? = null,
    @SerializedName("cbicdg_name_sr")
    val cbicdgNameSr: String? = null,
    @SerializedName("cbicdg_name_sh")
    val cbicdgNameSh: String? = null,
    @SerializedName("cbicdg_name_en")
    val cbicdgNameEn: String? = null,
    @SerializedName("cbicdg_name_ru")
    val cbicdgNameRu: String? = null,
    @SerializedName("cbicdg_status")
    val cbicdgStatus: Int? = null
)
