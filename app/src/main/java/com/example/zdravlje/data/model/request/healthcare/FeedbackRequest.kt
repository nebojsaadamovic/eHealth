package com.example.zdravlje.data.model.request.healthcare

data class FeedbackRequest(
    val response_id: String,
    val like: Boolean? = null,
    val answer: String? = null,
    val score: String? = null
)