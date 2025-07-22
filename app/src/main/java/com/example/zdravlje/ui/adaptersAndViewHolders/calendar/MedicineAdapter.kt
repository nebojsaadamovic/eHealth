package com.example.zdravlje.ui.adaptersAndViewHolders.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.ui.calendar.medicament.withoutrecipe.PeriodFromToFragment
import com.google.android.material.card.MaterialCardView
import com.nites.ezdravljeapp.R

data class MedicineAdapter(
    private var medicineList: List<PrescribedListMedicamentResponse>
) : RecyclerView.Adapter<MedicineAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = medicineList[position]
        holder.mtvTherapy.text = item.medicamentProtectedName + "; " + item.strength
        holder.mtvDescription.text =
            item.pharmaceuticalForm?.let { it.replaceFirstChar { c -> c.uppercase() } }

        println(item)
        println("kkkkkkkkkkkkkkkkkkkkkkk1111111111111111111")

        holder.clUserInfo.setOnClickListener() {
            val bundle = Bundle().apply {
                putParcelable("medicine_item", item)
                putString("title", "medicamenteFromERecipe")
            }

            val periodFromToFragment = PeriodFromToFragment()
            periodFromToFragment.arguments = bundle
            (holder.itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_options_container_view, periodFromToFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return medicineList.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvTherapy: TextView = itemView.findViewById(R.id.mtvTherapy)
        val clUserInfo: MaterialCardView = itemView.findViewById(R.id.clUserInfo)
        val mtvDescription: TextView = itemView.findViewById(R.id.mtvDescription)
    }

    fun updateData(newData:  List<PrescribedListMedicamentResponse>) {
        medicineList = newData
        notifyDataSetChanged()
    }
}
