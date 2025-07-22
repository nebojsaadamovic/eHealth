package com.example.zdravlje.ui.adaptersAndViewHolders.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.calendar.FamilyDoctor
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class DoctorAdapter(
    private val doctors: List<FamilyDoctor>,
    private val onItemClick: (FamilyDoctor) -> Unit
) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivDrAvatar: ImageView = view.findViewById(R.id.ivDrAvatar)
        private val tvNameDr: MaterialTextView = view.findViewById(R.id.tvNameDr)
        private val tvInstitution = view.findViewById<MaterialTextView>(R.id.tvInstitution)
        private val mtvSpeciality = view.findViewById<MaterialTextView>(R.id.mtvSpeciality)

        fun bind(doctor: FamilyDoctor) {
            ivDrAvatar.setImageResource(R.drawable.ic_dr_avatar)
            tvNameDr.text = doctor.name ?: ""
            tvInstitution.text = doctor.institution ?: ""
            mtvSpeciality.text = doctor.specialty ?: ""

            itemView.setOnClickListener {
                onItemClick(doctor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        holder.bind(doctors[position])
    }

    override fun getItemCount(): Int = doctors.size
}