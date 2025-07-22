package com.example.zdravlje.ui.adaptersAndViewHolders.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.chat.SearchContactResponse
import com.nites.ezdravljeapp.R

class ChatSearchAdapter(private var personList: MutableList<SearchContactResponse>, private val onItemClick: (SearchContactResponse) -> Unit) :
    RecyclerView.Adapter<ChatSearchAdapter.PersonViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_person, parent, false)
        return PersonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = personList[position]
        holder.bind(person)

        holder.itemView.setOnClickListener {
            onItemClick(person)
        }
    }


    fun updateData(newPersonList: List<SearchContactResponse>) {
        personList.clear()
        personList.addAll(newPersonList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = personList.size

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sgcoNameTextView: TextView = itemView.findViewById(R.id.sgco_name)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)

        fun bind(person: SearchContactResponse) {
            sgcoNameTextView.text = person.sgco_name
            if (person.email.equals("") || person.email==null){
                tvEmail.text = person.municipality+" "+person.birth_year
            }else{
                tvEmail.text = person.email
            }

        }
    }
}
