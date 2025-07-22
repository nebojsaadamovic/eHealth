package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.healthcare.MedicineCompareItem
import com.example.zdravlje.data.model.response.healthcare.InteractionMedicineResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

data class MedicamenteInteractionAdapter(
    private var itemList: List<InteractionMedicineResponse>? = emptyList(),
    private var parentFragmentManager: FragmentManager,
    private val context: Context,
) : RecyclerView.Adapter<MedicamenteInteractionAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        setViewModels()
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_interactions_medicamente, parent, false)
        return RecyclerViewHolder(view)
    }

    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = itemList?.get(position)
        holder.mtvName.text = item?.name
        holder.mtvInn.text = item?.inn

        holder.mtvAddCompare.setOnClickListener {
            val currentList = healthcareViewModel.listNameInn.value?.toMutableList() ?: mutableListOf()
            currentList.add(MedicineCompareItem(item?.name ?: "", item?.inn ?: ""))
            healthcareViewModel.listNameInn.value = currentList
        }
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvName: MaterialTextView = itemView.findViewById(R.id.mtvName)
        val mtvInn: MaterialTextView = itemView.findViewById(R.id.mtvInn)
        val mtvAddCompare: MaterialTextView = itemView.findViewById(R.id.mtvAddCompare)
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(context)
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel =
            ViewModelProvider(context as ViewModelStoreOwner, healthcareViewModelFactory)
                .get(HealthcareViewModel::class.java)
    }

    fun updateData(newData: List<InteractionMedicineResponse>) {
        itemList = newData
        notifyDataSetChanged()
    }
}