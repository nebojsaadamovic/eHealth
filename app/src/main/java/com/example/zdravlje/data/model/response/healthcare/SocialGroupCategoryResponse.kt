package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class SocialGroupCategoryResponse(
    @SerializedName("sgcat_id")
    val sgcatId: Int? = null,
    @SerializedName("sgcat_status")
    val sgcatStatus: String? = null,
    @SerializedName("sgcat_name_sh")
    val sgcatNameSh: String? = null,
    @SerializedName("sgcat_name_sr")
    val sgcatNameSr: String? = null,
    @SerializedName("sgcat_name_en")
    val sgcatNameEn: String? = null,
    @SerializedName("sgcat_name_ru")
    val sgcatNameRu: String? = null,
    @SerializedName("sg")
    val socialGroups: List<SocialGroup>? = null
)

data class SocialGroup(
    @SerializedName("sg_id")
    val sgId: Int? = null,
    @SerializedName("sg_name")
    val sgName: String? = null,
    @SerializedName("sg_description")
    val sgDescription: String? = null,
    @SerializedName("sg_social_group_category_id")
    val sgSocialGroupCategoryId: Int? = null,
    @SerializedName("sg_time_created")
    val sgTimeCreated: Long? = null,
    @SerializedName("sg_image")
    val sgImage: String? = null,
    @SerializedName("sg_status")
    val sgStatus: String? = null,
    @SerializedName("broj_pratilaca_grupe")
    val brojPratilacaGrupe: Int? = null,
    @SerializedName("is_group_followed")
    val isGroupFollowed: Int? = null,
    @SerializedName("sgm")
    val socialGroupMessages: List<SocialGroupMessage>? = null
)

data class SocialGroupMessage(
    @SerializedName("sgm_id")
    val sgmId: Int? = null,
    @SerializedName("sgm_title")
    val sgmTitle: String? = null,
    @SerializedName("sgm_message")
    val sgmMessage: String? = null,
    @SerializedName("sgm_social_group_contact_id")
    val sgmSocialGroupContactId: Int? = null,
    @SerializedName("sgm_social_group_id")
    val sgmSocialGroupId: Int? = null,
    @SerializedName("sgm_status")
    val sgmStatus: String? = null,
    @SerializedName("sgm_time_created")
    val sgmTimeCreated: Long? = null,
    @SerializedName("broj_lajkova_poruke")
    val brojLajkovaPoruke: Int? = null,
    @SerializedName("is_message_liked")
    val isMessageLiked: Int? = null
)
