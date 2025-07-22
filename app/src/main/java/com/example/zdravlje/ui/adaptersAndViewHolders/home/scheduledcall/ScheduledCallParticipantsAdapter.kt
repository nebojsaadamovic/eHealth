package com.example.zdravlje.ui.adaptersAndViewHolders.home.scheduledcall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.dashboard.future.Participant
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.nites.ezdravljeapp.R

class ScheduledCallParticipantsAdapter(
    private var participants: List<Participant> ?= null,
    private val calendarViewModel: CalendarViewModel ?= null,
    private val onParticipantRemovedListener: OnParticipantRemovedListener? = null
) : RecyclerView.Adapter<ScheduledCallParticipantsAdapter.RecyclerViewHolder>() {

    interface OnParticipantRemovedListener {
        fun onParticipantRemoved(updatedList: List<Participant>)
    }

    private val participantsList: MutableList<Participant> = participants?.toMutableList() ?: mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participiant, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = participantsList[position]
        holder.mtvName.text = item.sgco_name
        holder.mtvEmail.text = item.email

        holder.ivRemove.setOnClickListener {
            removeItem(position)
        }
    }

    override fun getItemCount(): Int = participantsList.size

    fun removeItem(position: Int) {
        if (position in participantsList.indices) {
            participantsList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, participantsList.size)
            onParticipantRemovedListener?.onParticipantRemoved(participantsList)
        }
    }

    fun updateData(newList: List<Participant>) {
        participantsList.clear()
        participantsList.addAll(newList)

        val mappedList = participantsList.map {
            com.example.zdravlje.data.model.response.chat.Participant(
                sgco_id = it.sgco_id?.toInt(),
                email = it.email,
                sgco_name = it.sgco_name,
                creator = it.creator.toString()
            )
        }
        calendarViewModel?.selectedParticipants = mappedList
        notifyDataSetChanged()
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvName: TextView = itemView.findViewById(R.id.mtvName)
        val mtvEmail: TextView = itemView.findViewById(R.id.mtvEmail)
        val ivRemove: ImageView = itemView.findViewById(R.id.ivRemove)
    }
}
