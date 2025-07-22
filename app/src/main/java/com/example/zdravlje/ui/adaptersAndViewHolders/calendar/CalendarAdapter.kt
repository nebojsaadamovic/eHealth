package com.example.zdravlje.ui.adaptersAndViewHolders.calendar

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class CalendarAdapter(
    private val days: List<Pair<String, Boolean>>,
    private val eventsMap: Map<String, DailyEvents>,
    private val currentMonth: Int,
    private val currentYear: Int,
    private val onEventClickListener: OnEventClickListener
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    interface OnEventClickListener {
        fun onEventClick(dailyEvents: DailyEvents)
    }

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.date_text)
        val cardWrapper: MaterialCardView = itemView.findViewById(R.id.cardWrapper)
        val eventContainer: LinearLayout = itemView.findViewById(R.id.eventContainer)
        val mtvConsumptionMedicine: MaterialTextView = itemView.findViewById(R.id.mtvConsumptionMedicine)
        val mtvAvailableTherapy: MaterialTextView = itemView.findViewById(R.id.mtvAvailableTherapy)
        val mtvDoctorsAppointment: MaterialTextView = itemView.findViewById(R.id.mtvDoctorsAppointment)
        val mtvPersonaActivity: MaterialTextView = itemView.findViewById(R.id.mtvPersonaActivity)
        val mtvScheduledMeeting: MaterialTextView = itemView.findViewById(R.id.mtvScheduledMeeting)
        val mtvThreeDot: MaterialTextView = itemView.findViewById(R.id.mtvThreeDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.calendar_day_item, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val (date, isCurrentMonth) = days[position]
        holder.dateText.text = date

        val formattedDate =
            formatDateToKey(currentMonth + 1, currentYear, holder.dateText.text.toString())

        val dailyEvents = eventsMap[formattedDate]

        dailyEvents?.let { eventsList ->
            if (isCurrentMonth) {
                displayEventDetails(holder, eventsList.events)
                holder.cardWrapper.setOnClickListener {
                    onEventClickListener.onEventClick(eventsList)
                }
            }
        }
    }

    private fun displayEventDetails(holder: CalendarViewHolder, events: List<Event>) {
        val maxEventsToShow = 4

        val addedEventTypes = mutableSetOf<Int>()
        var sizeRow=0

        events.take(maxEventsToShow).forEach { event ->
            val eventTextView = MaterialTextView(holder.itemView.context).apply {

                event.eventTypeId?.let {
                    if (addedEventTypes.add(it)) {
                        sizeRow += 1
                    }
                }

                when (event.eventTypeId) {
                    1 -> {
                        holder.mtvConsumptionMedicine.visibility=View.VISIBLE
                        holder.mtvConsumptionMedicine.setBackgroundColor(ContextCompat.getColor(context, R.color.medicineColor))
                    }

                    2 -> {
                        holder.mtvDoctorsAppointment.visibility=View.VISIBLE
                        holder.mtvDoctorsAppointment.setBackgroundColor(ContextCompat.getColor(context, R.color.appointmentColor))
                    }

                    3 -> {
                        holder.mtvAvailableTherapy.visibility=View.VISIBLE
                        holder.mtvAvailableTherapy.setBackgroundColor(ContextCompat.getColor(context, R.color.therapyColor))
                    }

                    4 -> {
                        holder.mtvPersonaActivity.visibility=View.VISIBLE
                        holder.mtvPersonaActivity.setBackgroundColor(ContextCompat.getColor(context, R.color.activityColor))
                    }

                    5 -> {
                        setBackgroundColor(ContextCompat.getColor(context, R.color.screeningColor))
                    }

                    6 -> {
                        if (event.isPastTime() && !event.isToday()) {
                            setBackgroundColor(ContextCompat.getColor(context, R.color.appointmentColor))
                        }else{
                            setBackgroundColor(ContextCompat.getColor(context, R.color.eventRealisedColor))
                        }
                    }

                    7->{
                        holder.mtvScheduledMeeting.visibility=View.VISIBLE
                        holder.mtvScheduledMeeting.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_7694FF))
                    }
                }
            }

            if (sizeRow > 4) {
                holder.mtvScheduledMeeting.visibility=View.GONE
                holder.mtvThreeDot.visibility = View.VISIBLE
            }
            holder.eventContainer.addView(eventTextView)
        }
    }

    override fun getItemCount(): Int = days.size

    private fun formatDateToKey(currentMonth: Int, currentYear: Int, day: String): String {
        return String.format("%04d-%02d-%02d", currentYear, currentMonth, day.toInt())
    }

    fun Int.toPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
        ).toInt()
    }
}
