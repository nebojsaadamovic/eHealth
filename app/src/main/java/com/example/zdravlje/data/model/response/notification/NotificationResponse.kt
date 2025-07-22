package com.example.zdravlje.data.model.response.notification

import android.util.Log
import com.example.zdravlje.data.model.response.dashboard.future.Participant
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class NotificationResponse(
    @SerializedName("notification")
    val notification: NotificationData,
    @SerializedName("notification_type")
    val notificationType: NotificationType,
    @SerializedName("sender_type")
    val senderType: String?
)

data class NotificationData(
    @SerializedName("not_id")
    val notId: Int,
    @SerializedName("not_notification_type_id")
    val notNotificationTypeId: Int,
    @SerializedName("not_status")
    var notStatus: Int,
    @SerializedName("not_payload")
    val notPayload: String,
    @SerializedName("not_time_expected_arival")
    val notTimeExpectedArrival: String,

    ) {
    fun getEventPayload(): EventPayload? {
        return try {
            // deserializacija notPayload u EventPayload
            val gson = Gson()
            gson.fromJson(notPayload, EventPayload::class.java)
        } catch (e: JsonSyntaxException) {
            Log.e("NotificationData", "Failed to parse notPayload", e)
            null
        }
    }

}

data class Mandatoryy(
    @SerializedName("doziranje")
    val doziranje: String? = null,
    @SerializedName("erx_doziranje")
    val erxDoziranje: String? = null,
    @SerializedName("lek_id")
    val lekId: String? = null,
    @SerializedName("prepisan_flag")
    val prepisanFlag: Int? = null
)

data class NotificationType(
    @SerializedName("nottyp_name")
    val nottypName: String,
    @SerializedName("nottyp_id")
    val nottypId: Int
)

data class EventPayload(
    @SerializedName("event")
    val event: Event,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("shortText")
    val shortText: String,
    @SerializedName("longText")
    val longText: String,
    val participants: List<Participant>,
    val emails: List<String>,
    val vcc_id: String
)

data class Event(
    @SerializedName("eve_id")
    val eveId: Int,
    @SerializedName("eve_icon")
    val eveIcon: String?,
    @SerializedName("eve_icon_extension")
    val eveIconExtension: String?,
    @SerializedName("eve_title")
    var eveTitle: String,
    @SerializedName("eve_description")
    val eveDescription: String?,
    @SerializedName("eve_start_time")
    val eveStartTime: Long,
    @SerializedName("eve_end_time")
    val eveEndTime: Long,
    @SerializedName("eve_calendar_id")
    val eveCalendarId: Int,
    @SerializedName("eve_event_type_id")
    val eveEventTypeId: Int,
    @SerializedName("eve_on_click_action_url")
    val eveOnClickActionUrl: String?,
    @SerializedName("eve_on_click_payload")
    val eveOnClickPayload: EventClickPayload?,
    @SerializedName("eve_on_click_confirm_dialog_text")
    val eveOnClickConfirmDialogText: String?,
    @SerializedName("eve_view_in_calendar")
    val eveViewInCalendar: Int,
    @SerializedName("eve_location")
    val eveLocation: String?,
    @SerializedName("eve_status")
    val eveStatus: Int,
    @SerializedName("eve_realized")
    val eveRealized: Int,
    @SerializedName("eve_external")
    val eveExternal: Int,
    @SerializedName("eve_external_last_time")
    val eveExternalLastTime: Long?,
) : Serializable {
    fun isPastTime(): Boolean {
        val currentTime = ZonedDateTime.now(ZoneId.systemDefault())
        val eventStartTimeLong = eveStartTime.toString().toLongOrNull()
        if (eventStartTimeLong != null) {
            val eventStartTimeInstant = Instant.ofEpochSecond(eventStartTimeLong)
            val eventStartTimeZoned = eventStartTimeInstant.atZone(ZoneId.systemDefault())
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = format.format(Date.from(eventStartTimeInstant))
            return eventStartTimeZoned.isBefore(currentTime)
        }
        return false
    }


    fun isToday(): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.format(Calendar.getInstance().time)
        val eventStartDate = sdf.format(sdf.parse(eveStartTime.toString())!!)
        return currentDate == eventStartDate
    }

    fun isFuture(): Boolean {
        val currentTime = Calendar.getInstance().time
        val eventStartTimeDate = try {
            val timestamp = eveStartTime.toString().toLong() * 1000
            Date(timestamp)
        } catch (e: Exception) {
            null
        }
        return eventStartTimeDate?.after(currentTime) ?: false
    }
}

data class EventClickPayload(
    @SerializedName("date")
    val date: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("exam_type")
    val examType: String?,
    @SerializedName("doctor_name")
    val doctorName: String?,
    @SerializedName("speciality_name")
    val specialityName: String?,
    @SerializedName("institution_name")
    val institutionName: String?,
    @SerializedName("day_of_week")
    val dayOfWeek: String?,
    @SerializedName("date_created")
    val dateCreated: String?,
    @SerializedName("mandatory")
    val mandatory: Mandatoryy? = null,
    val participants: List<Participant>? = null,
    val vcc_id: String? = null
) : Serializable

class EveOnClickPayloadDeserializer : JsonDeserializer<Any> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Any? {
        return try {
            // Pokušavamo da parsiramo kao objekat
            json?.asJsonObject?.let {
                Gson().fromJson(it, EventClickPayload::class.java)
            } ?: json?.asString // Ako nije objekat, tretiramo ga kao string
        } catch (e: JsonSyntaxException) {
            // Ako parsiranje kao objekat nije uspelo, vraćamo kao string
            json?.asString ?: ""
        }
    }
}

