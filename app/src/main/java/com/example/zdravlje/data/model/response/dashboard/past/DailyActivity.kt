package com.example.zdravlje.data.model.response.dashboard.past

import com.google.gson.annotations.SerializedName

data class DailyActivity(
    @SerializedName("activityDate") val activityDate: String,
    @SerializedName("activities") val activities: List<Activity>
)