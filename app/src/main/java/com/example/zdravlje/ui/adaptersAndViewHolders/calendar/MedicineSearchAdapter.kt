package com.example.zdravlje.ui.adaptersAndViewHolders.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.calendar.MedicineResponse
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class MedicineSearchAdapter(
    private val context: Context,
    private val medicineList: List<MedicineResponse>,
    private val onClickItem: (MedicineResponse) -> Unit,
) : RecyclerView.Adapter<MedicineSearchAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mtvTheraphy: MaterialTextView = view.findViewById(R.id.mtvTheraphy)
        val mtvTheraphyDesc: MaterialTextView = view.findViewById(R.id.mtvTheraphyDesc)
        val clTheraphy: ConstraintLayout = view.findViewById(R.id.clTheraphy)

        @SuppressLint("SetTextI18n")
        fun bind(medicine: MedicineResponse) {
            mtvTheraphy.text = medicine.cbmedalims_naziv_leka + " " + medicine.cbmedalims_oblik_i_doza_leka
            mtvTheraphyDesc.text = medicine.cbmedalims_inn
            clTheraphy.setOnClickListener {
                onClickItem(medicine)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine_search, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(medicineList[position])
    }

    override fun getItemCount(): Int = medicineList.size
}
