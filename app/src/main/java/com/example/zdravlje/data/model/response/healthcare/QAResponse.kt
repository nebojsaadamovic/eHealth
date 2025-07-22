package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class QAResponse(
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("citeng_id")
    val citengId: Int? = null,
    @SerializedName("topic")
    val topic: String? = null,
    @SerializedName("citeng_institution_name")
    val citengInstitutionName: String? = null,
    @SerializedName("citeng_question")
    val citengQuestion: String? = null,
    @SerializedName("citeng_answer")
    val citengAnswer: String? = null,
    @SerializedName("citeng_generated")
    val citengGenerated: String? = null,
    @SerializedName("citeng_like_dislike")
    val citengLikeDislike: Int? = null,
    @SerializedName("citeng_answered")
    val citengAnswered: String? = null,
)