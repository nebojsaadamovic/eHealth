package com.example.zdravlje.ui.adaptersAndViewHolders.home.fordelete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.dashboard.past.Activity
import com.nites.ezdravljeapp.R

class EventAdapter(private val events: List<Activity>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.mtvEventTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.mtvEventDesc)
        private val timeTextView: TextView = itemView.findViewById(R.id.mtvEventTime)
        private val eventImage: ImageView = itemView.findViewById(R.id.ivEventIcon)

        fun bind(event: Activity) {
            event.eventIcon
            titleTextView.text = event.eventTitle
            descriptionTextView.text = event.eventDescription
            timeTextView.text = event.eventStartTime

            descriptionTextView.textSize = 16f

            descriptionTextView.setTextColor(ContextCompat.getColor(itemView.context,R.color.blue_1C93BE))

            //postavljanje slika i boje teksta na osnovu eventType vrednosti

            when (event.eventType) {
                1 -> {
                    val iconResource = R.drawable.ic_medicine
                    val textColor = R.color.medicineColor
                    setIconAndTextColor(iconResource, textColor)
                }
                2 -> {
                    val iconResource = R.drawable.ic_appointment
                    val textColor = R.color.appointmentColor
                    setIconAndTextColor(iconResource, textColor)
                }
                3 -> {
                    val iconResource = R.drawable.ic_terapy
                    val textColor = R.color.therapyColor
                    setIconAndTextColor(iconResource, textColor)
                }
                4 -> {
                    val iconResource = R.drawable.ic_activity
                    val textColor = R.color.activityColor
                    setIconAndTextColor(iconResource, textColor)
                }
                5 -> {
                    val iconResource = R.drawable.ic_screening
                    val textColor = R.color.screeningColor
                    setIconAndTextColor(iconResource, textColor)
                }
                6 -> {
                    val iconResource = R.drawable.ic_event_realised
                    val textColor = R.color.eventRealisedColor
                    setIconAndTextColor(iconResource, textColor)
                }
                else -> {
                    val iconResource = R.drawable.ic_message
                    val textColor = R.color.base_color
                    setIconAndTextColor(iconResource, textColor)
                }
            }

        }

        fun setIconAndTextColor(iconResource: Int, textColorResource: Int) {
            if (iconResource != 0) {
                eventImage.setImageResource(iconResource)
            } else {
                eventImage.visibility = View.GONE
            }

            val textColor = ContextCompat.getColor(itemView.context, textColorResource)
            titleTextView.setTextColor(textColor)
        }
    }
}