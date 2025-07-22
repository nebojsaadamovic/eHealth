package com.example.zdravlje.ui.adaptersAndViewHolders.chat.info

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class ParticipantAdapter(
    private var participants: List<String>
) : RecyclerView.Adapter<ParticipantAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_person, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = participants[position]
        holder.mtvNameValue.text = item
    }

    override fun getItemCount(): Int {
        return participants.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvNameValue: MaterialTextView = itemView.findViewById(R.id.sgco_name)
        val mtvEmailValue: MaterialTextView = itemView.findViewById(R.id.tvEmail)
    }

    fun updateData(newParticipants: List<String>) {
        participants = newParticipants
        notifyDataSetChanged()
    }


    fun getAllParticipants(): List<String> {
        return participants
    }
}