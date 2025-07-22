package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.AccessMedicalCartonResponse
import com.nites.ezdravljeapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class AccesMedicalCartonAdapter(
    private var accesMeddicalCartonArray: Array<AccessMedicalCartonResponse>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<AccesMedicalCartonAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): AccesMedicalCartonAdapter.RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_access_doctor_medical_carton, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccesMedicalCartonAdapter.RecyclerViewHolder, position: Int) {
        val item = accesMeddicalCartonArray[position]
        holder.textViewRegNumInstitutions.text =item.institutionCode
        holder.textViewInstitutions.text = item.institutionName
        holder.textViewDoctor.text = item.doctorFullName
        val createDate = item.createDate
        val parts = createDate?.split("T")
        if (parts?.size == 2) {
            val rawDate = parts[0]
            val rawTime = parts[1].substring(0, 5)
            val formattedDate = formatDate(rawDate)
            holder.textViewDate.text = formattedDate
            holder.textViewTime.text = rawTime
        }
    }

    override fun getItemCount(): Int {
        return accesMeddicalCartonArray.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewRegNumInstitutions: TextView = itemView.findViewById(R.id.mtvRegNumInstitutionItemValue)
        val textViewInstitutions: TextView = itemView.findViewById(R.id.mtvInstitutionNameItemValue)
        val textViewDoctor: TextView = itemView.findViewById(R.id.mtvDoctorItemValue)
        val textViewDate: TextView = itemView.findViewById(R.id.mtvDateItemValue)
        val textViewTime: TextView = itemView.findViewById(R.id.mtvTimeItemValue)
    }

    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return ""
            val outputFormat = SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}