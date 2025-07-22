package com.example.zdravlje.data.model.response.login

data class GlobalError(
    var code: Int? = 0,
    var message: String? = "",
    var name: String? = "",
    var status: Int? = 0,
    var type: String? = ""
)