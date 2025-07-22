package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.MedicalCartonResponse
import com.example.zdravlje.ui.healthcare.medicalcarton.XrayAndDoctorsReportFragment
import com.nites.ezdravljeapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class MedicalCartonAdapter(
    private var cartonArray: List<MedicalCartonResponse>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<MedicalCartonAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicalCartonAdapter.RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine_carton, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = cartonArray[position]
        val dateTime = item.caseDate
        val parts = dateTime?.split(" ")
        val rawDate = parts?.get(0)
        val time = parts?.get(1)
        val timeWithoutSeconds = time?.split(":")?.let { "${it[0]}:${it[1]}" }
        val formattedDate = rawDate?.let { formatDate(it) }
        holder.textViewDateItem.text = formattedDate
        holder.textViewTimeItem.text = timeWithoutSeconds
        holder.mtvHeaderItem.text = item.caseInstitution
        holder.textViewOrganizationalUnit.text = item.detail?.admissionCabinetName
        holder.textViewDoctor.text = item.detail?.admissionDoctor
        holder.textViewTypeReferalle.text =
            "${item.detail?.referralType ?: ""} (${item.detail?.referralResponseTypeName ?: ""})"

        holder.clItemTimeSlot.setOnClickListener {
            val xrayAndDoctorsReportFragment = XrayAndDoctorsReportFragment(item)
            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, xrayAndDoctorsReportFragment)
                .addToBackStack(null).commit()
        }
    }

    override fun getItemCount(): Int {
        return cartonArray.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mtvHeaderItem: TextView = itemView.findViewById(R.id.mtvHeaderItem)
        val textViewDateItem: TextView = itemView.findViewById(R.id.mtvDateItemValue)
        val textViewTimeItem: TextView = itemView.findViewById(R.id.mtvTimeItemValue)
        val textViewOrganizationalUnit: TextView =
            itemView.findViewById(R.id.mtvOrganizationalUnitItemValue)
        val textViewDoctor: TextView = itemView.findViewById(R.id.mtvDoctorItemValue)
        val textViewTypeReferalle: TextView = itemView.findViewById(R.id.mtvTypeReferraleItemValue)
        val clItemTimeSlot: ConstraintLayout = itemView.findViewById(R.id.clItemTimeSlot)
    }

    fun formatDate(
        inputDate: String,
        inputPattern: String = "yyyy-MM-dd",
        outputPattern: String = "dd.MM.yyyy."
    ): String {
        return try {
            val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
            val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDate
        }
    }

    fun sortUpdatedData(newData: List<MedicalCartonResponse>) {
        cartonArray = emptyList()
        this.cartonArray = newData
        notifyDataSetChanged()
    }

    fun updateData(newData: List<MedicalCartonResponse>) {
        val oldSize = cartonArray.size
        cartonArray = cartonArray + newData
        notifyItemRangeInserted(oldSize, newData.size)
    }

    fun clearData() {
        cartonArray = emptyList()
        notifyDataSetChanged()
    }

}