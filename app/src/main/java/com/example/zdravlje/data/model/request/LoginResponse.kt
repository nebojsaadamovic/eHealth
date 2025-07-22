package com.example.zdravlje.data.model.request

data class LoginResponse(
    var message: String?="",
    var token: String?="",
    var data: String?="",
    var type: Int?=0
)