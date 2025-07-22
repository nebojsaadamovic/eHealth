package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(
    @SerializedName("rec_id")
    val recId: Int? = null,
    @SerializedName("rec_title")
    val recTitle: String? = null,
    @SerializedName("rec_content")
    val recContent: String? = null,
    @SerializedName("rec_sgco_receiver_id")
    val receiverId: Int? = null,
    @SerializedName("rec_sgco_creator_id")
    val creatorId: Int? = null,
    @SerializedName("rec_status")
    val status: Int? = null,
    @SerializedName("rec_date")
    val recDate: Long? = null,
    @SerializedName("avatar_encoded_value")
    val avatarEncodedValue: String? = null,
    @SerializedName("avatar_ext")
    val avatarExt: String? = null
)