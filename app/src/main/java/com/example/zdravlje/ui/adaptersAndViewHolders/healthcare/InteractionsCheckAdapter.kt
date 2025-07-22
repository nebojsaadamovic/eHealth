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
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

data class InteractionsCheckAdapter(
    private var itemList: List<MedicineCompareItem> = emptyList(),
    private var parentFragmentManager: FragmentManager,
    private var context: Context
) : RecyclerView.Adapter<InteractionsCheckAdapter.RecyclerViewHolder>() {

    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_interactions_check, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        setViewModels()
        val item = itemList?.get(position)
        holder.mtvName.text = item?.name
        holder.mtvType.text = item?.inn
        holder.mtvRemove.setOnClickListener() {
            val currentList = healthcareViewModel.listNameInn.value?.toMutableList()
            currentList?.removeAt(position)
            healthcareViewModel.listNameInn.value = currentList
        }
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvName: MaterialTextView = itemView.findViewById(R.id.mtvName)
        val mtvType: MaterialTextView = itemView.findViewById(R.id.mtvType)
        val mtvRemove: MaterialTextView = itemView.findViewById(R.id.mtvRemove)
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
}