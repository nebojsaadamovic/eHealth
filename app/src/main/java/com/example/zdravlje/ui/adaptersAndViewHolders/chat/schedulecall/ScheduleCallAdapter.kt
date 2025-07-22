package com.example.zdravlje.ui.adaptersAndViewHolders.chat.schedulecall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.chat.Participant
import com.nites.ezdravljeapp.R

class ScheduleCallAdapter(
    private var dataList: List<Participant>,
    private var selectedParticipants: List<Participant> = emptyList()
) : RecyclerView.Adapter<ScheduleCallAdapter.RecyclerViewHolder>() {

    private var selectedItems: MutableSet<Participant> = mutableSetOf()

    init {
        selectedItems.addAll(selectedParticipants)
    }

    var updateListe = false
    private var listener: OnParticipantListener? = null

    interface OnParticipantListener {
        fun onParticipantSelected(selectedList: List<Participant>)
    }

    fun setOnParticipantListener(listener: OnParticipantListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participiant_schedule_call, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = dataList[position]
        if (updateListe) {
            holder.checkBox.buttonDrawable = null
            holder.checkBox.isEnabled = false
        }
        holder.cbParticipantValue.text = item.sgco_name

        holder.checkBox.isChecked = selectedItems.contains(item)
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }
            listener?.onParticipantSelected(selectedItems.toList())
        }

        if (item.email != null && item.sgco_id == 0) {
            holder.mtvParticipiantEmailValue.visibility = View.VISIBLE
            holder.mtvParticipiantEmailValue.text = item.email
            holder.checkBox.isChecked = true
        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbParticipantValue: TextView = itemView.findViewById(R.id.cbParticipiantValue)
        val checkBox: CheckBox = itemView.findViewById(R.id.cbParticipiantValue)
        val mtvParticipiantEmailValue: TextView =
            itemView.findViewById(R.id.mtvParticipiantEmailValue)
    }

    fun updateData(newDataParticipant: List<Participant>) {
        dataList = newDataParticipant
        notifyDataSetChanged()
        updateListe = true
    }

    fun updateFilterData(newDataParticipant: List<Participant>) {
        dataList = newDataParticipant
        notifyDataSetChanged()
    }
}
