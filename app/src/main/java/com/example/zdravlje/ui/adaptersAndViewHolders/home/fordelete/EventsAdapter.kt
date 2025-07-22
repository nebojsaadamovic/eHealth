package com.example.zdravlje.ui.adaptersAndViewHolders.home.fordelete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.dashboard.past.DailyActivity
import com.nites.ezdravljeapp.R

class EventsAdapter(private val events: List<DailyActivity>) : RecyclerView.Adapter<EventsAdapter.EventDateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventDateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_date, parent, false)
        return EventDateViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventDateViewHolder, position: Int) {
        val eventDate = events[position]
        holder.bind(eventDate)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class EventDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val eventsRecyclerView: RecyclerView = itemView.findViewById(R.id.eventsRecyclerView)

        fun bind(dailyActivity: DailyActivity) {
            dateTextView.text = dailyActivity.activityDate

            // Postavljanje RecyclerView-a unutar svakog datuma
            eventsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            eventsRecyclerView.adapter = EventAdapter(dailyActivity.activities)
        }
    }
}