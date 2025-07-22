package com.example.zdravlje.ui.adaptersAndViewHolders.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.model.response.dashboard.event_payload.AppointmentEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.CaseEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.event_payload.TherapyEventPayloadParcelable
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.notification.EveOnClickPayloadDeserializer
import com.example.zdravlje.data.model.response.notification.EventClickPayload
import com.example.zdravlje.data.model.response.notification.EventPayload
import com.example.zdravlje.data.model.response.notification.NotificationResponse
import com.example.zdravlje.ui.calendar.BottomSheetEventInfoFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetRealizedUnrealizedActivityFragment
import com.example.zdravlje.ui.home.dashboard.event_details.DoctorsAppointmentFragment
import com.example.zdravlje.ui.home.dashboard.event_details.PersonalDataActivity
import com.example.zdravlje.ui.home.dashboard.event_details.RealisedVisitedDoctorActivity
import com.example.zdravlje.ui.home.dashboard.event_details.ScheduledCallInfoFragment
import com.example.zdravlje.ui.home.dashboard.event_details_fragments.AvailableTherapyInPharmacyFragment
import com.example.zdravlje.ui.home.dashboard.event_details_fragments.DrugConsumptionFragment
import com.example.zdravlje.ui.notification.InfoBottomSheetFragment
import com.example.zdravlje.ui.notification.NotificationViewModel
import com.example.zdravlje.utilities.DateUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.nites.ezdravljeapp.R

class NotificationAdapter(
    private var notificationsList: List<NotificationResponse> = mutableListOf(),
    private val notificationViewModel: NotificationViewModel,
    private val userPin: String,
    private val fragmentManager: FragmentManager,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val calendarChangeListener: BottomSheetEventInfoFragment.OnCalendarChangeListener? = null,
    private val valueChangeListener: BottomSheetRealizedUnrealizedActivityFragment.OnValueChangeListener? = null
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notificationsList[position]

        val gson = GsonBuilder().registerTypeAdapter(
            EventClickPayload::class.java,
            EveOnClickPayloadDeserializer()
        ).create()

        val eventPayload: EventPayload? = try {
            val jsonElement = JsonParser.parseString(item.notification.notPayload)

            if (jsonElement.isJsonObject) {
                gson.fromJson(jsonElement, EventPayload::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("NotificationAdapter", "Error parsing notPayload: ${e.message}")
            null
        }

        val eveTitle = eventPayload?.event?.eveTitle
            ?: holder.itemView.context.getString(R.string.notification_adapter_data_available)
        holder.notificationDetailTitle.text = eveTitle
        val shortText = eventPayload?.shortText
            ?: holder.itemView.context.getString(R.string.notification_adapter_data_available)
        holder.notificationDetailText.text = shortText
        holder.notificationTitle.text = item.notificationType.nottypName
        holder.notificationDetailDate.text =
            DateUtils.convertYYYYMMDDHHssToDotDDMMYYYY(item.notification.notTimeExpectedArrival)

        val statusMsg = item.notification.notStatus
            ?: holder.itemView.context.getString(R.string.notification_adapter_data_available)
        if (statusMsg == 1) {
            holder.ivReadMsg.visibility = View.GONE
        } else {
            holder.ivReadMsg.visibility = View.VISIBLE
        }

        holder.notificationSupTitle.text = when (item.senderType?.trim()?.toIntOrNull()) {
            0 -> context.getString(R.string.notification_adapter_sender_type_no_event)
            1 -> context.getString(R.string.notification_adapter_sender_type_eRecept)
            2 -> context.getString(R.string.notification_adapter_sender_type_eKarton)
            3 -> context.getString(R.string.notification_adapter_sender_type_eDosije)
            4 -> context.getString(R.string.notification_adapter_sender_type_eHealth)
            5 -> context.getString(R.string.notification_adapter_sender_type_all)
            else -> context.getString(R.string.notification_adapter_sender_type_eHealth)
        }

        holder.ivReadMsg.setOnClickListener() {
            notificationViewModel.setNotificationRead(
                NotificationReadRequest(
                    pin = userPin, notIds = listOf(item.notification.notId)
                )
            )

            notificationViewModel.setNotificationReadLiveData.observe(
                lifecycleOwner,
                Observer { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("NotificationAdapter", "Response body: $responseBody")
                    } else {
                        Log.e("NotificationAdapter", "Response not successful: ${response.code()}")
                    }
                })

            Toast.makeText(
                context,
                holder.itemView.context.getString(R.string.notification_adapter_msg_read),
                Toast.LENGTH_SHORT
            ).show()
            val infoBottomSheetFragment = InfoBottomSheetFragment(eventPayload)
            infoBottomSheetFragment.show(fragmentManager, infoBottomSheetFragment.tag)
            notificationsList[position].notification.notStatus = 1
            notifyItemChanged(position)
        }

        val event = eventPayload?.event
        if (event != null) {
            if (event.eveRealized == 0 && event.isPastTime()) {
                holder.ivWarning.visibility = View.VISIBLE
            }
        }
        holder.ivWarning.setOnClickListener() {
            if (event != null) {
                when (event.eveEventTypeId) {
                    1 -> {
                        if (event.eveRealized == 0 && event.isPastTime()) {
                            val newEventFuture = Event(
                                eventId = event.eveId,
                                eventTitle = event.eveTitle,
                                eventStartTime = DateUtils.convertTimestampToDateWithMiddleLine(
                                    event.eveStartTime
                                ),
                                eventOnClickPayload = eventPayload.event.eveOnClickPayload.toString()
                            )

                            val bottomSheetEventInfo =
                                BottomSheetEventInfoFragment(newEventFuture, item, "Notification")
                            if (calendarChangeListener != null) {
                                bottomSheetEventInfo.setCalendarChangeListener(
                                    calendarChangeListener
                                )
                            }
                            bottomSheetEventInfo.show(fragmentManager, bottomSheetEventInfo.tag)
                        } else if (event.eveRealized == 1 && event.isPastTime()) {
                            val gson = Gson()
                            val eventClickPayload = eventPayload.event.eveOnClickPayload
                            val eventClickPayloadJson = gson.toJson(eventClickPayload)
                            try {
                                val medicineEventPayload = gson.fromJson(
                                    eventClickPayloadJson,
                                    MedicineEventPayload::class.java
                                )

                                val bundle = Bundle().apply {
                                    putSerializable("medicineEventPayload", medicineEventPayload)
                                    putSerializable("eventNotification", event)
                                }

                                val drugConsumptionFragment = DrugConsumptionFragment()
                                drugConsumptionFragment.arguments = bundle
                                openFragment(drugConsumptionFragment)
                            } catch (e: JsonSyntaxException) {
                                Log.e("NotificationAdapter", "Error parsing JSON: ${e.message}")
                            }
                        }
                    }

                    2 -> {
                        if (event.eveRealized == 0 && event.isPastTime()) {
                            val newEventFuture = Event(
                                eventId = event.eveId,
                                eventTitle = event.eveTitle,
                                eventStartTime = DateUtils.convertTimestampToDateWithMiddleLine(
                                    event.eveStartTime
                                ),
                                eventOnClickPayload = eventPayload.event.eveOnClickPayload.toString()
                            )

                            val bottomSheet =
                                BottomSheetRealizedUnrealizedActivityFragment(newEventFuture)

                            if (valueChangeListener != null) {
                                bottomSheet.setValueChange(valueChangeListener)
                            }
                            bottomSheet.show(fragmentManager, bottomSheet.tag)
                        } else if (event.eveRealized == 1 && event.isPastTime()) {
                            val gson = Gson()
                            val eventClickPayload = eventPayload.event.eveOnClickPayload
                            val eventClickPayloadJson = gson.toJson(eventClickPayload)

                            val appointmentEventPayload =
                                gson.fromJson(
                                    eventClickPayloadJson,
                                    AppointmentEventPayload::class.java
                                )
                            val bundle = Bundle().apply {
                                putParcelable(
                                    "doctors_appointment_details",
                                    appointmentEventPayload
                                )
                            }

                            val fragment = DoctorsAppointmentFragment()
                            openFragment(fragment)
                            fragment.arguments = bundle
                        }
                    }

                    3 -> {
                        if (event.eveRealized == 0 && event.isPastTime()) {

                            val newEventFuture = Event(
                                eventId = event.eveId,
                                eventTitle = event.eveTitle,
                                eventStartTime = DateUtils.convertTimestampToDateWithMiddleLine(
                                    event.eveStartTime
                                ),
                                eventOnClickPayload = eventPayload.event.eveOnClickPayload.toString()
                            )

                            val bottomSheet =
                                BottomSheetRealizedUnrealizedActivityFragment(newEventFuture)
                            bottomSheet.apply {
                                arguments = Bundle().apply {
                                    putString(
                                        "KEY_HEADER_TITLE",
                                        holder.itemView.context.getString(R.string.notification_adapter_data_available)
                                    )
                                    putString(
                                        "KEY_TITLE",
                                        holder.itemView.context.getString(R.string.notification_adapter_available_questions_therapy)
                                    )
                                    putString(
                                        "KEY_NAME",
                                        holder.itemView.context.getString(R.string.notification_adapter_available_therapy)
                                    )
                                }
                            }
                            if (valueChangeListener != null) {
                                bottomSheet.setValueChange(valueChangeListener)
                            }
                            bottomSheet.show(fragmentManager, bottomSheet.tag)

                        } else if (event.eveRealized == 1 && event.isPastTime()) {
                            val gson = Gson()
                            val eventClickPayload = eventPayload.event.eveOnClickPayload
                            val eventClickPayloadJson = gson.toJson(eventClickPayload)

                            val therapyEventPayload =
                                gson.fromJson(
                                    eventClickPayloadJson,
                                    TherapyEventPayloadParcelable::class.java
                                )

                            val bundle = Bundle().apply {
                                putParcelable("therapyEventPayload", therapyEventPayload)
                            }
                            val availableTherapyInPharmacyFragment =
                                AvailableTherapyInPharmacyFragment()
                            openFragment(availableTherapyInPharmacyFragment)
                            availableTherapyInPharmacyFragment.arguments = bundle
                        }
                    }

                    4 -> {
                        if (event.eveRealized == 0 && event.isPastTime()) {
                            val newEventFuture = Event(
                                eventId = event.eveId,
                                eventTitle = event.eveTitle,
                                eventDescription = event.eveDescription,
                                eventStartTime = DateUtils.formatTimestampToYYYYMMddHHmmMiddleLine(
                                    event.eveStartTime
                                ),
                                eventOnClickPayload = eventPayload.event.eveOnClickPayload.toString()
                            )


                            val bottomSheet =
                                BottomSheetRealizedUnrealizedActivityFragment(newEventFuture)
                            bottomSheet.apply {
                                arguments = Bundle().apply {
                                    putString(
                                        "KEY_HEADER_TITLE",
                                        holder.itemView.context.getString(R.string.notification_adapter_personal_activity)
                                    )
                                    putString(
                                        "KEY_TITLE",
                                        holder.itemView.context.getString(R.string.notification_adapter_available_questions_personal_activity)
                                    )
                                    putString(
                                        "KEY_NAME",
                                        holder.itemView.context.getString(R.string.notification_adapter_personal_activity)
                                    )
                                }
                            }

                            if (valueChangeListener != null) {
                                bottomSheet.setValueChange(valueChangeListener)
                            }
                            bottomSheet.show(fragmentManager, bottomSheet.tag)
                        } else if (event.eveRealized == 1 && event.isPastTime()) {
                            val intent =
                                Intent(holder.itemView.context, PersonalDataActivity::class.java)
                            intent.putExtra("event", event)
                            holder.itemView.context.startActivity(intent)
                        }
                    }

                    6 -> {
                        if (event.eveRealized == 0 && event.isPastTime()) {

                            val newEventFuture = Event(
                                eventId = event.eveId,
                                eventTitle = event.eveTitle,
                                eventStartTime = DateUtils.convertTimestampToDateWithMiddleLine(
                                    event.eveStartTime
                                ),
                                eventOnClickPayload = eventPayload.event.eveOnClickPayload.toString()
                            )

                            val bottomSheet =
                                BottomSheetRealizedUnrealizedActivityFragment(newEventFuture)
                            bottomSheet.apply {
                                arguments = Bundle().apply {
                                    putString(
                                        "KEY_HEADER_TITLE",
                                        holder.itemView.context.getString(R.string.notification_adapter_appointment_completed)
                                    )
                                    putString(
                                        "KEY_TITLE",
                                        holder.itemView.context.getString(R.string.notification_adapter_questions_appointment_completed)
                                    )
                                    putString(
                                        "KEY_NAME",
                                        holder.itemView.context.getString(R.string.notification_adapter_doctor_exam)
                                    )
                                }
                            }
                            if (valueChangeListener != null) {
                                bottomSheet.setValueChange(valueChangeListener)
                            }
                            bottomSheet.show(fragmentManager, bottomSheet.tag)
                        } else if (event.eveRealized == 1 && event.isPastTime()) {

                            val gson = Gson()
                            val eventClickPayload = eventPayload.event.eveOnClickPayload
                            val eventClickPayloadJson = gson.toJson(eventClickPayload)

                            val caseEventPayload =
                                gson.fromJson(eventClickPayloadJson, CaseEventPayload::class.java)

                            val intent = Intent(
                                holder.itemView.context,
                                RealisedVisitedDoctorActivity::class.java
                            )
                            intent.putExtra("caseEventPayload", caseEventPayload)
                            intent.putExtra("event", event)
                            holder.itemView.context.startActivity(intent)
                        }
                    }

                    7 -> {
                        val fragment = ScheduledCallInfoFragment.newInstance(null, event)
                        openFragment(fragment)
                    }
                }
            }
        }

        if (userPin.isNullOrEmpty()){
            holder.ivReadMsg.visibility = View.GONE
            holder.ivWarning.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationTitle: TextView = itemView.findViewById(R.id.notificationTitle)
        val notificationSupTitle: TextView = itemView.findViewById(R.id.notificationSupTitle)
        val notificationDetailTitle: TextView = itemView.findViewById(R.id.notificationDetailTitle)
        val notificationDetailDate: TextView = itemView.findViewById(R.id.notificationDetailDate)
        val notificationDetailText: TextView = itemView.findViewById(R.id.notificationDetailText)
        val ivReadMsg: ImageView = itemView.findViewById(R.id.ivReadMsg)
        val clContent: ConstraintLayout = itemView.findViewById(R.id.clContent)
        val ivWarning: ImageView = itemView.findViewById(R.id.ivWarning)
    }

    private fun openFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.notification_fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<NotificationResponse>) {
        notificationsList = newData
        notifyDataSetChanged()
    }
}
