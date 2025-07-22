package com.example.zdravlje.data.model.response.healthcare

data class InteractionHistoryItem(
    val id: Int,
    val response_id: String,
    val question: String,
    val answer: String,
    val score: Int?,
    val edited_answer: String?,
    val edited_score: Int?,
    val like: Boolean?,
    val time: String,
    val user: String,
    val user_type: String,
    val patient_id: String
)