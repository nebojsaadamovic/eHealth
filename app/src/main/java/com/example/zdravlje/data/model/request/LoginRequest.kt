package com.example.zdravlje.data.model.request

data class LoginRequest(
    val username: String? = "",
    val password: String? = "",
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
)
