package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.Employer
import com.example.zdravlje.data.model.response.healthcare.SickLeaveNoteResponse
import com.nites.ezdravljeapp.R

class EmployersAdapter(
    private var listEmployer: List<Employer>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<EmployersAdapter.RecyclerViewHolder>() {

    private var listener: OnInstitutionListener? = null
    private var selectedItem: Employer? = null

    interface OnInstitutionListener {
        fun onInstitutionSelected(employerName: Employer, sickLeaveNotes: List<SickLeaveNoteResponse>)
    }

    fun setOnInstitutionListener(listener: OnInstitutionListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institution_arr, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = listEmployer[position]
        holder.radioButtonEmployerName.text = item.employerName

        holder.radioButtonEmployerName.isChecked = (item == selectedItem)
        holder.radioButtonEmployerName.setOnClickListener {

            val previousSelectedPosition = listEmployer.indexOf(selectedItem)
            selectedItem = item
            listener?.onInstitutionSelected(item, item.sickLeaveNotes)
            if (previousSelectedPosition != -1) {
                notifyItemChanged(previousSelectedPosition)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return listEmployer.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButtonEmployerName: RadioButton = itemView.findViewById(R.id.rbInstitutionNameValue)
    }
}
