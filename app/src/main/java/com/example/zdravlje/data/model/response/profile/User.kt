package com.example.zdravlje.data.model.response.profile

data class User(
    val sgco_id: Int,
    val user_id: Int,
    val sgco_name: String,
    val email: String,
    val avt_encoded_value: String? = null,
    val avt_ext: String? = null,
    val status: Int,
    val last_activity: Int? = null,
    val type_id: Int,
    val can_see_profile: Int,
    val sharing: Sharing
)
