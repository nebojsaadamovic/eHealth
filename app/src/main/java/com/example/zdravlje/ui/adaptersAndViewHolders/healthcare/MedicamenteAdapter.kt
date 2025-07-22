package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.MedicamenteResponse
import com.example.zdravlje.ui.healthcare.prevention.medicaments.MedicamentDetailsInfoFragment
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class MedicamenteAdapter(
    private var data: List<MedicamenteResponse>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<MedicamenteAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicamente, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = data[position]
        holder.mtvItem.text = item.cbmedProtectedName
        holder.ivItem.setOnClickListener() {
            openFragment(MedicamentDetailsInfoFragment(item))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvItem: MaterialTextView = itemView.findViewById(R.id.mtvItem)
        val ivItem: ImageView = itemView.findViewById(R.id.ivItem)
    }

    private fun openFragment(nameFragment: Fragment) {
        fragmentManager.beginTransaction().replace(R.id.home_fragment_container_view, nameFragment)
            .addToBackStack(null)
            .commit()
    }

    fun updateData(newData: List<MedicamenteResponse>) {
        data = newData
        notifyDataSetChanged()
    }
}