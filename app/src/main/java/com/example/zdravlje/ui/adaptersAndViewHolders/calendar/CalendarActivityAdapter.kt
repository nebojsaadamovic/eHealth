package com.example.zdravlje.ui.adaptersAndViewHolders.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.ui.interfaces.ActivityClickListener
import com.google.android.material.card.MaterialCardView
import com.nites.ezdravljeapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarActivityAdapter(
    var activities: List<Event>,
    private val clickListener: ActivityClickListener
) : RecyclerView.Adapter<CalendarActivityAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.bind(activity)
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    fun updateEvent(updatedEvent: Event) {
        val eventIndex = activities.indexOfFirst { it.eventId == updatedEvent.eventId }
        if (eventIndex != -1) {
            activities = activities.toMutableList().apply { set(eventIndex, updatedEvent) }
            notifyItemChanged(eventIndex)
        }
    }

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val titleTextView: TextView = itemView.findViewById(R.id.mtvEventTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.mtvEventDesc)
        private val timeTextView: TextView = itemView.findViewById(R.id.mtvEventTime)
        private val eventImage: ImageView = itemView.findViewById(R.id.ivEventIcon)
        private val mcvItemWrapper: MaterialCardView = itemView.findViewById(R.id.itemWrapper)

        fun bind(event: Event) {
            event.eventIcon
            descriptionTextView.text = event.eventTitle
            timeTextView.text = event.eventStartTime?.let { parseDateTo_ddMMyyyy(it) }

            when (event.eventTypeId) { // for  NextOnScheduleCalendarFragment
                1 -> {
                    val iconResource: Int
                    val titleTextColor: Int
                    val descTextColor: Int
                    var itemWrapper: Int? = null
                    if (event.eventRealized == 0 && event.isPastTime()) {
                        iconResource = R.drawable.ic_medicine_activity_red
                        titleTextColor = R.color.black
                        descTextColor = R.color.black
                        itemWrapper = R.color.green_light_FFFDF5
                    } else if (event.eventRealized == 2 && event.isPastTime()) {
                        iconResource = R.drawable.ic_activity_calendar_red
                        titleTextColor = R.color.red_warning_E75656
                        descTextColor = R.color.black
                    } else if (event.eventRealized == 1 && event.isPastTime()) {
                        iconResource = R.drawable.ic_correct
                        titleTextColor = R.color.medicineColor
                        descTextColor = R.color.black
                    } else {
                        iconResource = R.drawable.ic_medicine
                        titleTextColor = R.color.medicineColor
                        descTextColor = R.color.black
                    }
                    println(event.eventId)
                    println(event.eventTypeId)
                    val title =
                        itemView.context.getString(R.string.calendar_activity_adapter_consumption_medicamente)
                    setIconAndTextColorAnText(iconResource, titleTextColor, title, descTextColor,itemWrapper)
                }

                2 -> {
                    val iconResource = R.drawable.ic_event_realised
                    val titleTextColor = R.color.appointmentColor
                    val descTextColor = R.color.black
                    val title =
                        itemView.context.getString(R.string.calendar_activity_adapter_doctors_appointment)
                    setIconAndTextColorAnText(iconResource, titleTextColor, title, descTextColor)
                }

                3 -> {
                    var iconResource = R.drawable.ic_terapy
                    var titleTextColor = R.color.therapyColor
                    var descTextColor = R.color.black

                    var title = itemView.context.getString(R.string.activity_adapter_available_therapy_pharmacy)

                    if (event.isPastTime() && !event.isToday()  && event.eventRealized==1 ) {
                        titleTextColor = R.color.green_25C3A6
                        descTextColor = R.color.black
                        iconResource = R.drawable.erecept
                        title = itemView.context.getString(R.string.activity_adapter_realized_e_recipe)
                    }
                    setIconAndTextColorAnText(iconResource, titleTextColor, title, descTextColor)
                }

                4 -> {
                    val iconResource: Int
                    val titleTextColor: Int
                    val descTextColor: Int
                    var itemWrapper: Int? = null
                    if (event.eventRealized == 0 && event.isPastTime()) {
                        iconResource = R.drawable.ic_medicine_activity_red
                        titleTextColor = R.color.black
                        descTextColor = R.color.black
                        itemWrapper = R.color.green_light_FFFDF5
                    } else if (event.eventRealized == 2 && event.isPastTime()) {
                        iconResource = R.drawable.ic_activity_calendar_red
                        titleTextColor = R.color.black
                        descTextColor = R.color.black
                    } else if (event.eventRealized == 1 && event.isPastTime()) {
                        iconResource = R.drawable.ic_correct
                        val iconDrawable = ContextCompat.getDrawable(itemView.context, iconResource)
                        if (iconDrawable != null) {
                            eventImage.setImageDrawable(iconDrawable)
                            eventImage.setColorFilter(
                                itemView.context.getColor(R.color.activityColor),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                        titleTextColor = R.color.activityColor
                        descTextColor = R.color.black
                    } else {
                        iconResource = R.drawable.ic_activity
                        titleTextColor = R.color.activityColor
                        descTextColor = R.color.black
                    }
                    val title = itemView.context.getString(R.string.calendar_activity_adapter_personal_activity)
                    setIconAndTextColorAnText(iconResource, titleTextColor, title, descTextColor, itemWrapper)
                }


                5 -> {
                    val iconResource = R.drawable.ic_screening
                    val titleTextColor = R.color.screeningColor
                    val descTextColor = R.color.black
                    val title =
                        itemView.context.getString(R.string.calendar_activity_adapter_appointment_screening_examination)
                    setIconAndTextColorAnText(iconResource, titleTextColor, title, descTextColor)
                }

                6 -> {
                    val iconResource = R.drawable.ic_event_realised
                    var titleTextColor = R.color.eventRealisedColor
                    var descTextColor = R.color.black
                    val title =
                        itemView.context.getString(R.string.calendar_activity_adapter_completed_doctor_visit)
                    if (event.isPastTime()) {
                        titleTextColor = R.color.appointmentColor
                        descTextColor = R.color.black
                    }
                    setIconAndTextColorAnText(iconResource, titleTextColor, title, descTextColor)
                }

                7 -> {
                    val iconResource: Int
                    val titleTextColor: Int
                    val descTextColor: Int
                        iconResource = R.drawable.ic_scheduled_call
                        titleTextColor = R.color.purple_7694FF
                        descTextColor = R.color.black
                    val title = itemView.context.getString(R.string.calendar_activity_adapter_scheduled_call)
                    setIconAndTextColorAnText(iconResource, titleTextColor, title, descTextColor)
                }

            }
        }

        fun setIconAndTextColorAnText(
            iconResource: Int,
            titleTextColorResource: Int,
            title: String,
            descTextColorResource: Int,
            itemWrapper: Int? = null
        ) {
            if (iconResource != 0) {
                eventImage.setImageResource(iconResource)
            } else {
                eventImage.visibility = View.GONE
            }

            val titleTextColor = ContextCompat.getColor(itemView.context, titleTextColorResource)
            val descTextColor = ContextCompat.getColor(itemView.context, descTextColorResource)
            descriptionTextView.setTextColor(descTextColor)
            titleTextView.setTextColor(titleTextColor)
            titleTextView.text = title
            if (itemWrapper != null) {
                mcvItemWrapper.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, itemWrapper)
            }else{
                mcvItemWrapper.backgroundTintList =
                    ContextCompat.getColorStateList(itemView.context, R.color.white)
            }
        }

        fun parseDateTo_ddMMyyyy(input: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            return try {
                val date = inputFormat.parse(input)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                ""
            }
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val activity = activities[position]
                clickListener.onActivityClicked(activity)
            }
        }
    }
}