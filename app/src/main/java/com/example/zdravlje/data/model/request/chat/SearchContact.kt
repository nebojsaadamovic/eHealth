package com.example.zdravlje.data.model.request.chat

data class SearchContact(
    val terms: String? = "",
    val include_friends: Int? = 2,
    val limit: Int? = 100,
    val offset: Int? = 0,
    val pin: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)