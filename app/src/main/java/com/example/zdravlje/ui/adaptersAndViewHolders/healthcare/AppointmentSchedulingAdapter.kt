package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.AppointmentResponse
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

data class AppointmentSchedulingAdapter(
    private var dataList: List<AppointmentResponse>,
    private val fragmentManager: FragmentManager,
    private val onItemClick: (AppointmentResponse) -> Unit
) : RecyclerView.Adapter<AppointmentSchedulingAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_scheduling, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = dataList[position]

        holder.clHeader.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.gray_D9D8E1))
        holder.vLine.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.gray_D9D8E1))

         holder.mtvDateValue.text = item.date
         holder.mtvTimeValue.text = item.time
         holder.mtvDoctorValue.text = item.doctorName
         holder.mtvInstitutionValue.text = item.institutionName
         holder.mtvTypeExaminationValue.text = item.examType

        holder.ivDeleteHistory.setOnClickListener(){
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvDateValue : MaterialTextView = itemView.findViewById(R.id.mtvDateValue)
        val mtvTimeValue : MaterialTextView = itemView.findViewById(R.id.mtvTimeValue)
        val mtvDoctorValue : MaterialTextView = itemView.findViewById(R.id.mtvDoctorValue)
        val mtvInstitutionValue : MaterialTextView = itemView.findViewById(R.id.mtvInstitutionValue)
        val mtvTypeExaminationValue : MaterialTextView = itemView.findViewById(R.id.mtvTypeExaminationValue)
        val ivDeleteHistory: ImageView = itemView.findViewById(R.id.ivDeleteHistory)
        val clHeader : ConstraintLayout = itemView.findViewById(R.id.clHeader)
        val vLine : View = itemView.findViewById(R.id.vLine)
    }
}