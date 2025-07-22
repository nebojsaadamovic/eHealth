package com.example.zdravlje.data.model.response.dashboard.future

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Event(
    @SerializedName("eve_id")
    val eventId: Int? = null,
    @SerializedName("eve_icon")
    val eventIcon: String? = null,
    @SerializedName("eve_icon_extension")
    val eventIconExtension: String? = null,
    @SerializedName("eve_title")
    val eventTitle: String? = null,
    @SerializedName("eve_description")
    val eventDescription: String? = null,
    @SerializedName("eve_start_time")
    val eventStartTime: String? = null,
    @SerializedName("eve_end_time")
    val eventEndTime: String? = null,
    @SerializedName("eve_calendar_id")
    val eventCalendarId: Int? = null,
    @SerializedName("eve_on_click_action_url")
    val eventOnClickActionUrl: String? = null,
    @SerializedName("eve_on_click_payload")
    var eventOnClickPayload: String? = null,
    @SerializedName("eve_on_click_confirm_dialog_text")
    val eventOnClickConfirmDialogText: String? = null,
    @SerializedName("eve_view_in_calendar")
    val eventViewInCalendar: Int? = null,
    @SerializedName("eve_status")
    val eventStatus: Int? = null,
    @SerializedName("eve_event_type_id")
    val eventTypeId: Int? = null,
    @SerializedName("eve_realized")
    var eventRealized: Int? = null,
    @SerializedName("pin")
    var pin: String? = null,
    val timezone: String? = "Europe/Belgrade" //TimeZone.getDefault().id
) : Serializable {

    fun getEventPayload(): EventPayload? {
        return eventOnClickPayload?.let {
            Gson().fromJson(it, EventPayload::class.java)
        }
    }

    fun isPastTime(): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        val eventStartTimeDate = sdf.parse(eventStartTime)
        val isPast = eventStartTimeDate?.before(currentTime) ?: false
        return isPast
    }


    fun isToday(): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Calendar.getInstance().time)
        val eventStartDate = sdf.format(sdf.parse(eventStartTime)!!)
        return currentDate == eventStartDate
    }

    fun isFuture(): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        val eventStartTimeDate = eventStartTime?.let { sdf.parse(it) }
        return eventStartTimeDate?.after(currentTime) ?: false
    }
}

data class EventPayload(
    val participants: List<Participant>,
    val emails: List<String>,
    val vcc_id: String
)

data class Participant(
    val sgco_id: String?,
    val sgco_name: String,
    val email: String?,
    val creator: Int
) : Serializable