package com.example.zdravlje.ui.adaptersAndViewHolders.chat.chatAddContact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.chat.Participant
import com.nites.ezdravljeapp.R

class ChatMyContactRadioGroupAdapter(
    private var contactsList: List<Participant>,
    private var className: String? = null
) : RecyclerView.Adapter<ChatMyContactRadioGroupAdapter.RecyclerViewHolder>() {


    private var selectedItem: Participant? = null
    private var selectedPosition: Int = -1
    private var listener: OnParticipantListener? = null


    interface OnParticipantListener {
        fun onParticipantSelected(selectedList: List<Participant>)
    }

    fun setOnParticipantListener(listener: OnParticipantListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_contact_radio_btn, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = contactsList[position]

        holder.radioButton.setOnCheckedChangeListener(null)
        holder.radioButton.isChecked = (position == selectedPosition)

        holder.radioButton.setOnCheckedChangeListener { _, isChecked ->
            val currentPosition = holder.adapterPosition
            if (isChecked && currentPosition != RecyclerView.NO_POSITION) {
                selectedItem = contactsList[currentPosition]
                selectedPosition = currentPosition
                notifyDataSetChanged()
                listener?.onParticipantSelected(listOf(contactsList[currentPosition]))
            }
        }

        holder.mtvSgcoNameValue.text = item.sgco_name
        if (className == "ChooseContactFragment") {
            holder.mtvEmailValue.visibility = View.GONE
        } else {
            holder.mtvEmailValue.text = item.email
        }
    }

    override fun getItemCount(): Int {
        return contactsList?.size ?: 0
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvSgcoNameValue: TextView = itemView.findViewById(R.id.mtvSgcoNameValue)
        val mtvEmailValue: TextView = itemView.findViewById(R.id.mtvEmailValue)
        val radioButton: RadioButton = itemView.findViewById(R.id.radioButtonName)
    }

    fun updateFilterData2(newDataParticipant: List<Participant>) {
        contactsList = newDataParticipant
        notifyDataSetChanged()
    }
    fun clearList() {
        contactsList = emptyList()
        notifyDataSetChanged()
    }

}
