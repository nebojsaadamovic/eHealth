package com.example.zdravlje.ui.adaptersAndViewHolders.chat.createGroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.chat.Participant
import com.nites.ezdravljeapp.R

data class ChatMyContactAdapter(
    private var contactsList: List<Participant>
) : RecyclerView.Adapter<ChatMyContactAdapter.RecyclerViewHolder>() {

    private var selectedItems: MutableSet<Participant> = mutableSetOf()
    private var listener: OnParticipantListener? = null

    interface OnParticipantListener {
        fun onParticipantSelected(selectedList: List<Participant>)
    }

    fun setOnParticipantListener(listener: OnParticipantListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_contact, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = contactsList?.get(position)

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (item != null) {
                    selectedItems.add(item)
                }
            } else {
                selectedItems.remove(item)
            }
            listener?.onParticipantSelected(selectedItems.toList())
        }

        if (item != null) {
            holder.mtvSgcoNameValue.text = item.sgco_name
            holder.mtvEmailValue.text = item.email
        }
    }

    override fun getItemCount(): Int {
        return contactsList?.size ?: 0
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvSgcoNameValue: TextView = itemView.findViewById(R.id.mtvSgcoNameValue)
        val mtvEmailValue: TextView = itemView.findViewById(R.id.mtvEmailValue)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxName)
    }

    fun updateFilterData2(newDataParticipant: List<Participant>) {
        contactsList = newDataParticipant
        notifyDataSetChanged()
    }
}
