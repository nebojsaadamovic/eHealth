package com.example.zdravlje.data.model.response.chat

import com.google.gson.annotations.SerializedName

data class ChatInfoPayload (
    val unread_message_count: Int,
    val chat_type: Int,
    val last_message_at: Int?
)

data class Feature(
    @SerializedName("gender")
    val gender: String,
    @SerializedName("municipality")
    val municipality: String,
    @SerializedName("age")
    val age: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("last_activity")
    val lastActivity: Int
)