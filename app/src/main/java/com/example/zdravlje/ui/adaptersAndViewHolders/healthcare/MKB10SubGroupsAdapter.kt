package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.healthcare.MKB10SubResponseGroups
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class MKB10SubGroupsAdapter (
    private var data: List<MKB10SubResponseGroups>
) : RecyclerView.Adapter<MKB10SubGroupsAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sub_medicamente_groups, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = data[position]
        holder.mtvItemSubGroups.text = item.cbicdNameSh
        holder.mtvCodeMKBSubGroups.text = item.cbicdCode
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvItemSubGroups: MaterialTextView = itemView.findViewById(R.id.mtvDescMKBSubGroups)
        val mtvCodeMKBSubGroups: MaterialTextView = itemView.findViewById(R.id.mtvCodeMKBSubGroups)
    }

    fun updateData(newData: List<MKB10SubResponseGroups>) {
        data = newData
        notifyDataSetChanged()
    }
}
