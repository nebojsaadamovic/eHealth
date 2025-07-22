package com.example.zdravlje.data.model.response.profile

import com.google.gson.annotations.SerializedName

data class AccountSynchronizeResponse(
    @SerializedName("use_external_system")
    val useExternalSystem: String,

    @SerializedName("use_external_system_id")
    val useExternalSystemID: Int,

    @SerializedName("use_sync_started")
    val useSyncStarted: String? = null,

    @SerializedName("use_sync_completed")
    val useSyncCompleted: String? = null,

    @SerializedName("use_sync_error")
    val useSyncError: String? = null
)