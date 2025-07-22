package com.example.zdravlje.data.model.request.healthcare

import com.google.gson.annotations.SerializedName

data class MKB10SubResponseGroups(
    @SerializedName("cbicd_id")
    val cbicdId: Int? = null,
    @SerializedName("cbicd_code")
    val cbicdCode: String? = null,
    @SerializedName("cbicd_mkb_code_char_number")
    val cbicdMkbCodeCharNumber: Int? = null,
    @SerializedName("cbicd_name_sr")
    val cbicdNameSr: String? = null,
    @SerializedName("cbicd_name_sh")
    val cbicdNameSh: String? = null,
    @SerializedName("cbicd_name_en")
    val cbicdNameEn: String? = null,
    @SerializedName("cbicd_name_ru")
    val cbicdNameRu: String? = null,
    @SerializedName("cbicd_revised_code_char_number")
    val cbicdRevisedCodeCharNumber: Int? = null,
    @SerializedName("cbicd_icd10subgroup_id")
    val cbicdIcd10SubgroupId: Int? = null,
    @SerializedName("cbicd_status")
    val cbicdStatus: Int? = null,
    @SerializedName("cbicd_sex_limit")
    val cbicdSexLimit: Int? = null,
    @SerializedName("cbicd_cod_limit")
    val cbicdCodLimit: Int? = null,
    @SerializedName("cbicd_injury_flag")
    val cbicdInjuryFlag: Int? = null
)
