package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.Institution
import com.nites.ezdravljeapp.R

class InstitutionsArrAdapter(
    private var institutionArray: Array<Institution>,
    private val fragmentManager: FragmentManager,
    private val className: String? = null
) : RecyclerView.Adapter<InstitutionsArrAdapter.RecyclerViewHolder>() {

    interface OnInstitutionListener {
        fun onInstitutionSelected(institution: String?, institutionName: String)
    }

    private var listener: OnInstitutionListener? = null

    fun setOnInstitutionListener(listener: OnInstitutionListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institution_arr, parent, false)
        return RecyclerViewHolder(view)
    }

    private var selectedItem: Institution? = null

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = institutionArray[position]

        holder.radioButton.isChecked = item == selectedItem

        if (className == "Institution") {
            holder.textViewInstitutions.text = item.name
        }
        if (className == "MedicalCarton" || className == "MedicalCartonDateFragment" || className == "MedicalCartonFilter") {
            holder.textViewInstitutions.text = item.institutionName
        }

        holder.radioButton.isChecked = (item == selectedItem)
        holder.radioButton.setOnClickListener {
            val previousSelectedPosition = institutionArray.indexOf(selectedItem)
            selectedItem = item

            listener?.onInstitutionSelected(
                selectedItem?.institutionCode,
                selectedItem?.institutionName.toString()
            )
            if (previousSelectedPosition != -1) {
                notifyItemChanged(previousSelectedPosition)
            }

            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return institutionArray.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewInstitutions: TextView = itemView.findViewById(R.id.rbInstitutionNameValue)
        val radioButton: RadioButton = itemView.findViewById(R.id.rbInstitutionNameValue)
    }

    fun updateData(newDataInstition: Array<Institution>) {
        institutionArray = newDataInstition
        notifyDataSetChanged()
    }

    fun setSelectedInstitution(institution: Institution) {
        selectedItem = institutionArray.find { it.institutionCode == institution.institutionCode }
        notifyDataSetChanged()
    }
}