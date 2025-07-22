package com.example.zdravlje.data.model.response.dashboard.past

import com.google.gson.annotations.SerializedName

data class Activity(
    @SerializedName("eve_id") val eventId: Int,
    @SerializedName("eve_icon") val eventIcon: String,
    @SerializedName("eve_icon_extension") val eventIconExtension: String,
    @SerializedName("eve_title") val eventTitle: String,
    @SerializedName("eve_description") val eventDescription: String,
    @SerializedName("eve_start_time") val eventStartTime: String,
    @SerializedName("eve_end_time") val eventEndTime: String,
    @SerializedName("eve_calendar_id") val eventCalendarId: Int,
    @SerializedName("eve_on_click_action_url") val eventClickActionUrl: String,
    @SerializedName("eve_on_click_payload") val eventClickPayload: String,
    @SerializedName("eve_on_click_confirm_dialog_text") val eventClickConfirmDialogText: String,
    @SerializedName("eve_view_in_calendar") val eventViewInCalendar: Int,
    @SerializedName("eve_status") val eventStatus: Int,
    @SerializedName("eve_event_type_id") val eventType: Int,
    @SerializedName("eve_realized") val eventRealized: Int
)