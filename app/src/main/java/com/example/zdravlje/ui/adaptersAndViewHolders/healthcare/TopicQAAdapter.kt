package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.TopicQAResponse
import com.nites.ezdravljeapp.R

data class TopicQAAdapter(
    private var topicArray: Array<TopicQAResponse>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<TopicQAAdapter.RecyclerViewHolder>() {

    interface OnTopicQAListener {
        fun onTopicQASelected(topicName:String)
    }

    private var listener: OnTopicQAListener? = null
    fun setOnTopicQAListener(listener: OnTopicQAListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institution_arr, parent, false)
        return RecyclerViewHolder(view)
    }

    private var selectedItem: TopicQAResponse? = null

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = topicArray[position]

        holder.textViewInstitutions.text = item.topnameSh
        holder.radioButton.isChecked = (item == selectedItem)
        holder.radioButton.setOnClickListener {
            val previousSelectedPosition = topicArray.indexOf(selectedItem)
            selectedItem = item
            listener?.onTopicQASelected(item.topnameSh.toString())
            if (previousSelectedPosition != -1) {
                notifyItemChanged(previousSelectedPosition)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return topicArray.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewInstitutions: TextView = itemView.findViewById(R.id.rbInstitutionNameValue)
        val radioButton: RadioButton = itemView.findViewById(R.id.rbInstitutionNameValue)
    }

    fun updateData(newData: Array<TopicQAResponse>) {
        topicArray = newData
        notifyDataSetChanged()
    }
}