package com.example.zdravlje.ui.adaptersAndViewHolders.chat.createGroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nites.ezdravljeapp.R


data class ChatSelectedContactForGroupAdapter(
    private var contactsName: List<String>? = null
) : RecyclerView.Adapter<ChatSelectedContactForGroupAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_contact, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = contactsName?.get(position)

        holder.mtvSgcoName.text = item.toString()
        holder.mtvUserInicials.text =
            item?.split(" ")?.mapNotNull { it.firstOrNull()?.uppercase() }?.joinToString("")
    }

    override fun getItemCount(): Int {
        return contactsName?.size ?: 0
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvUserInicials: TextView = itemView.findViewById(R.id.mtvUserInicials)
        val mtvSgcoName: TextView = itemView.findViewById(R.id.mtvSgcoName)
    }

    fun updateFilterData(newDataParticipant: List<String>) {
        contactsName = newDataParticipant
        notifyDataSetChanged()
    }
}