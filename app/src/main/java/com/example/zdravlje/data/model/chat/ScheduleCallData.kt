package com.example.zdravlje.data.model.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleCallData(
    var desc: String? = null,
    var organyzer: String? = null,
    var date: String? = null,
    var timeFrom: String? = null,
    var timeTo: String? = null
) : Parcelable