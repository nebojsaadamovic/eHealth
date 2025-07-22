package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class InstitutionsResponse(
    val data: Data
)

data class Data(
    val online: Int,
    val online_new: Int,
    val systems: List<System>
)

data class System(
    val name: String,
    val status: String? = null,
    @SerializedName("health")
    val health: String? = null,
    @SerializedName("internal_id")
    val internalId: String? = null,
)
