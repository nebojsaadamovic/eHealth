package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.CategoryQAResponse
import com.nites.ezdravljeapp.R

data class CategoryQAAdapter(
    private var categoryArray: Array<CategoryQAResponse>,
    private val fragmentManager: FragmentManager,
    private val className: String? = null
) : RecyclerView.Adapter<CategoryQAAdapter.RecyclerViewHolder>() {

    interface OnCategoryQAListener {
        fun onCategoryQASelected(categoryName:String)
    }

    private var listener: OnCategoryQAListener? = null
    fun setOnCategoryQAListener(listener: OnCategoryQAListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institution_arr, parent, false)
        return RecyclerViewHolder(view)
    }

    private var selectedItem: CategoryQAResponse? = null

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = categoryArray[position]

        holder.textViewInstitutions.text = item.catNameSh
        holder.radioButton.isChecked = (item == selectedItem)
        holder.radioButton.setOnClickListener {
            val previousSelectedPosition = categoryArray.indexOf(selectedItem)
            selectedItem = item
            listener?.onCategoryQASelected(item.catNameSh.toString())
            if (previousSelectedPosition != -1) {
                notifyItemChanged(previousSelectedPosition)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return categoryArray.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewInstitutions: TextView = itemView.findViewById(R.id.rbInstitutionNameValue)
        val radioButton: RadioButton = itemView.findViewById(R.id.rbInstitutionNameValue)
    }

    fun updateData(newData: Array<CategoryQAResponse>) {
        categoryArray = newData
        notifyDataSetChanged()
    }
}