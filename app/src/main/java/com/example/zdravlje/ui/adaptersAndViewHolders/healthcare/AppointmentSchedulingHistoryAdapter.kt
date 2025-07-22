package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.EventHistoryDataResponse
import com.example.zdravlje.data.model.response.healthcare.OnClickPayloadItem
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.nites.ezdravljeapp.R

data class AppointmentSchedulingHistoryAdapter(
    private var dataList: List<EventHistoryDataResponse>,
    private val fragmentManager: FragmentManager,
) : RecyclerView.Adapter<AppointmentSchedulingHistoryAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_scheduling, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = dataList[position]

        holder.ivDeleteHistory.visibility = View.GONE
        holder.clHeader.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))

        val payloadObject = Gson().fromJson(item.payload, OnClickPayloadItem::class.java)

        holder.mtvDateValue.text = payloadObject.date
        holder.mtvTimeValue.text = payloadObject.time
        holder.mtvDoctorValue.text = payloadObject.doctorName
        holder.mtvInstitutionValue.text = payloadObject.institutionName
        holder.mtvTypeExaminationValue.text = payloadObject.examType
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvDateValue: MaterialTextView = itemView.findViewById(R.id.mtvDateValue)
        val mtvTimeValue: MaterialTextView = itemView.findViewById(R.id.mtvTimeValue)
        val mtvDoctorValue: MaterialTextView = itemView.findViewById(R.id.mtvDoctorValue)
        val mtvInstitutionValue: MaterialTextView = itemView.findViewById(R.id.mtvInstitutionValue)
        val mtvTypeExaminationValue: MaterialTextView =
            itemView.findViewById(R.id.mtvTypeExaminationValue)
        val ivDeleteHistory: ImageView = itemView.findViewById(R.id.ivDeleteHistory)
        val clHeader: ConstraintLayout = itemView.findViewById(R.id.clHeader)
        val vLine: View = itemView.findViewById(R.id.vLine)
    }
}