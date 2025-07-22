package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.PatmedNotes
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.PrescribedMedicamentOptionsFragment
import com.example.zdravlje.utilities.DateUtils
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import java.util.Calendar
import java.util.Date

class PrescribedListMedicamentsAdapter(
    private val prescribedListArr: Array<PrescribedListMedicamentResponse>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<PrescribedListMedicamentsAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescribedListMedicamentsAdapter.RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prescribed_list_medicament, parent, false)
        return RecyclerViewHolder(view)
    }
    private val currentList = prescribedListArr.toMutableList()

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = currentList[position]

        val gson = Gson()
        val patmedNotesJson = item.patmedNotes
        val payload: PatmedNotes? = gson.fromJson(patmedNotesJson, PatmedNotes::class.java)

        var isArrivalDateInRange = false

        payload?.arrivalDate?.let { arrivalDateString ->
            val currentDate = Date()
            val arrivalDate = DateUtils.stringToDate(arrivalDateString, "dd.MM.yyyy") ?: currentDate

            val calendar = Calendar.getInstance()
            calendar.time = currentDate

            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val sevenDaysBefore = calendar.time

            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val sevenDaysAfter = calendar.time

            isArrivalDateInRange = arrivalDate >= sevenDaysBefore && arrivalDate <= sevenDaysAfter
        }

        holder.textMedicamentNameItem.text = item.medicamentProtectedName
        if (payload?.drugStrength.isNullOrEmpty()){
            holder.textViewStrengthItem.text = item.dosage?.split(";")?.get(1)
        }else {
            holder.textViewStrengthItem.text = if (payload?.drugStrength.isNullOrEmpty()) "-        " else payload?.drugStrength
        }

        holder.textViewDosageItem.text =  if (payload?.dosage.isNullOrEmpty()) "-        " else payload?.dosage
        holder.textViewQuantityItem.text =if (payload?.quantity.isNullOrEmpty()) "-        " else payload?.quantity

        holder.textViewStatusItem.text = when (item.patmed_rcpt_status) {
            "1" -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_request_approved)
            "2" -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_request_completed)
            "4" -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_expired)
            "6" -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_requests_canceled)
            "100" -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_manual_entry)
            else -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_prescription_status_unknown)
        }

        holder.textViewExpiryDateItem.text = DateUtils.convertTimestampToDateDotddMMyyyy(item.startDate)

        val background =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_gray_top_radis_10)
                ?.mutate()
        when (item.patmed_rcpt_status) {
            "1" -> {
                if(isArrivalDateInRange){
                    background?.setTint(holder.itemView.context.getColor(R.color.yellow_light_FFEBA))
                }else{
                    background?.setTint(holder.itemView.context.getColor(R.color.orange_light_FDCEAB))
                }
            }
            "2" -> background?.setTint(holder.itemView.context.getColor(R.color.green_light_C6E0B4))
            "4" -> background?.setTint(holder.itemView.context.getColor(R.color.gray_B9B9BB))
            "6" -> background?.setTint(holder.itemView.context.getColor(R.color.pink_light_F8BABA))
            "100" -> background?.setTint(holder.itemView.context.getColor(R.color.blue_light_D9E1F2))
        }

        holder.clMedicamentNameItem.background = background

        holder.textViewPrescriptionTypeItem.text = when (item.prescriptionType) {
            "1" -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_prescription_type_e_therapy)
            "2" -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_prescription_type_e_prescription)
            "3" -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_prescription_type_no_prescription)
            else -> holder.itemView.context.getString(R.string.fragment_prescribed_list_medicaments_prescription_type_unknown)
        }

        holder.clItemTimeSlot.setOnClickListener {
            val prescribedMedicamentOptionsFragment = PrescribedMedicamentOptionsFragment(item)
            fragmentManager.beginTransaction().apply {
                replace(R.id.home_fragment_container_view, prescribedMedicamentOptionsFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textMedicamentNameItem: TextView = itemView.findViewById(R.id.clMedicamentNameItemValue)
        val textViewStrengthItem: TextView = itemView.findViewById(R.id.mtvStrengthItemValue)
        val textViewDosageItem: TextView = itemView.findViewById(R.id.mtvDosageItemValue)
        val textViewQuantityItem: TextView = itemView.findViewById(R.id.mtvQuantityItemValue)
        val textViewExpiryDateItem: TextView = itemView.findViewById(R.id.mtvExpiryDateItemValue)
        val textViewStatusItem: TextView = itemView.findViewById(R.id.mtvStatusItemValue)
        val textViewPrescriptionTypeItem: TextView =
            itemView.findViewById(R.id.mtvPrescriptionTypeItemValue)
        val clMedicamentNameItem: ConstraintLayout =
            itemView.findViewById(R.id.clMedicamentNameItem)
        val clItemTimeSlot: ConstraintLayout = itemView.findViewById(R.id.clItemTimeSlot)
    }

    fun addItems(newItems: List<PrescribedListMedicamentResponse>) {
        val startPosition = currentList.size
        currentList.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }
    fun setItems(newItems: Array<PrescribedListMedicamentResponse>) {
        currentList.clear()
        currentList.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clearItems() {
        currentList.clear()
        notifyDataSetChanged()
    }
}