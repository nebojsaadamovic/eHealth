package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.DiagnosisEntry
import com.nites.ezdravljeapp.R

class PrescribedListLimitationsAdapter(
    var dataArr : List<DiagnosisEntry>?=null,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<PrescribedListLimitationsAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescribedListLimitationsAdapter.RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_limitations, parent, false)
        return RecyclerViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = dataArr?.get(position)

        val background = holder.clHeaderItem.background
        if (background is LayerDrawable) {
            val layerDrawable = background as LayerDrawable
            val shapeDrawable = layerDrawable.getDrawable(0) as GradientDrawable
            shapeDrawable.setColor(ContextCompat.getColor(holder.itemView.context, R.color.blue_light_D9E1F2))
        }

        holder.mtvNameDiagnoseValue.text= item?.diagnosis
        holder.mtvParticipationValue.text= "${item?.participation}${"%"}"
        holder.mtvTimeFromItemValue.text= item?.yearsFrom
        holder.mtvTimeToItemValue.text= item?.yearsTo
        holder.mtvMaxQuantityValue.text= item?.quantity
    }

    override fun getItemCount(): Int {
        return dataArr?.size ?: 0
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val clHeaderItem: ConstraintLayout = itemView.findViewById(R.id.clHeaderItem)
        val mtvNameDiagnoseValue: TextView = itemView.findViewById(R.id.mtvNameDiagnoseValue)
        val mtvParticipationValue: TextView = itemView.findViewById(R.id.mtvParticipationValue)
        val mtvTimeFromItemValue: TextView = itemView.findViewById(R.id.mtvTimeFromItemValue)
        val mtvTimeToItemValue: TextView = itemView.findViewById(R.id.mtvTimeToItemValue)
        val mtvMaxQuantityValue: TextView = itemView.findViewById(R.id.mtvMaxQuantityItemValue)
    }
}