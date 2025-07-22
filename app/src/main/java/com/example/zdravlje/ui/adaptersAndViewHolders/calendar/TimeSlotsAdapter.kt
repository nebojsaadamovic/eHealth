package com.example.zdravlje.ui.adaptersAndViewHolders.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.calendar.Timeslot
import com.google.android.material.radiobutton.MaterialRadioButton
import com.nites.ezdravljeapp.R

class TimeSlotsAdapter(
    private val timeslot: List<Timeslot>,
    private val listener: OnRadioListener)
    : RecyclerView.Adapter<TimeSlotsAdapter.RecyclerViewHolder>() {

    interface OnRadioListener {
        fun onRadioChange(from: String, to:String, id: String)
    }
    private var selectedPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.textViewTimeSlot.text = "${timeslot[position].from} - ${timeslot[position].to}"
        holder.radioButton.isChecked = position == selectedPosition

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            listener.onRadioChange(timeslot[position].from ,timeslot[position].to, timeslot[position].id)
        }
    }

    override fun getItemCount(): Int {
        return timeslot.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTimeSlot: TextView = itemView.findViewById(R.id.mtvTimeSlot)
        val radioButton: MaterialRadioButton = itemView.findViewById(R.id.rbTimeSlot)
    }
}
