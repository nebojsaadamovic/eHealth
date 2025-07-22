package com.example.zdravlje.ui.adaptersAndViewHolders.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.ui.home.DashboardViewModel
import com.example.zdravlje.ui.interfaces.ActivityClickListener
import com.example.zdravlje.utilities.DateUtils
import com.example.zdravlje.utilities.DateUtils.getDateWithWeekName
import com.nites.ezdravljeapp.R

class ActivitiesAdapter(
    private val activities: List<DailyEvents>,
    private val clickListener: ActivityClickListener?=null,
    private val fragmentActivity: FragmentActivity,
    private val dashboardViewModel: DashboardViewModel
) : RecyclerView.Adapter<ActivitiesAdapter.ActivityDateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityDateViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_event_date, parent, false)
        return ActivityDateViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityDateViewHolder, position: Int) {
        val activityDate = activities[position]
        holder.bind(activityDate)

        holder.ivCalendarHome.setOnClickListener {
            DateUtils.showDatePickerDialog(holder.itemView.context, holder.dateTextView) { selectedDate ->
                holder.dateTextView.text = selectedDate
                dashboardViewModel.dateParem.value = selectedDate // ✅ ovo je sad ista instanca kao u fragmentu
            }
        }
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    inner class ActivityDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val ivCalendarHome: ImageView = itemView.findViewById(R.id.ivCalendarHome)
        private val eventsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.eventsRecyclerView)

        fun bind(activityDate: DailyEvents) {
            dateTextView.text = getDateWithWeekName(activityDate.eventDate, )

            // Postavljanje RecyclerView-a unutar svakog datuma
            eventsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            eventsRecyclerView.adapter =
                clickListener?.let { ActivityAdapter(activityDate.events, it) }
        }
    }
}