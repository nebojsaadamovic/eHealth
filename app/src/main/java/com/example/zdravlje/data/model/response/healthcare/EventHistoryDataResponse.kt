package com.example.zdravlje.data.model.response.healthcare

import com.google.gson.annotations.SerializedName

data class EventHistoryDataResponse(
    @SerializedName("eve_id")
    val id: Int,
    @SerializedName("eve_icon")
    val icon: String?,
    @SerializedName("eve_icon_extension")
    val iconExtension: String,
    @SerializedName("eve_title")
    val title: String,
    @SerializedName("eve_description")
    val description: String,
    @SerializedName("eve_start_time")
    val startTime: String,
    @SerializedName("eve_end_time")
    val endTime: String,
    @SerializedName("eve_calendar_id")
    val calendarId: Int,
    @SerializedName("eve_on_click_action_url")
    val actionUrl: String,
    @SerializedName("eve_on_click_payload")
    val payload: String,
    @SerializedName("eve_on_click_confirm_dialog_text")
    val confirmDialogText: String,
    @SerializedName("eve_view_in_calendar")
    val viewInCalendar: Int,
    @SerializedName("eve_status")
    val status: Int,
    @SerializedName("eve_event_type_id")
    val eventTypeId: Int,
    @SerializedName("eve_realized")
    val realized: Int
)


data class OnClickPayloadItem(
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("exam_type")
    val examType: String,
    @SerializedName("doctor_name")
    val doctorName: String,
    @SerializedName("speciality_name")
    val specialityName: String,
    @SerializedName("institution_name")
    val institutionName: String,
    @SerializedName("day_of_week")
    val dayOfWeek: String,
    @SerializedName("date_created")
    val dateCreated: String,
    @SerializedName("app_type")
    val appType: String,
    @SerializedName("app_type_text")
    val appTypeText: String
)