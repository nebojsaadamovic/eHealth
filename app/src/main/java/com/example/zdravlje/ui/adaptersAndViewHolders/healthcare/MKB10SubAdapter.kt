package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.healthcare.MKB10SubResponse
import com.example.zdravlje.data.model.request.healthcare.MKB10SubResponseGroups
import com.example.zdravlje.data.model.request.healthcare.PreventionDTO
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class MKB10SubAdapter(
    private var data: List<MKB10SubResponse>,
    private var userPin: String,
    private val fragmentManager: FragmentManager,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<MKB10SubAdapter.RecyclerViewHolder>() {

    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var mKB10SubGroupsAdapter: MKB10SubGroupsAdapter
    private var dataList: List<MKB10SubResponseGroups> = emptyList()

    var isClicked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sub_medicamente, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        setViewModels()
        val item = data[position]
        holder.mtvItemSub.text = item.cbicdsgNameSh
        holder.mtvCodeMKBSub.text = item.cbicdsgCode
        holder.ivItemSub.setOnClickListener {
            if (!isClicked) {
                holder.ivItemSub.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.ivItemSub.context,
                        R.drawable.ic_arrow_bottom
                    )
                )
                holder.clMKBSub.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                holder.rvMKB10SubListSub.visibility = View.VISIBLE

                healthcareViewModel.getSubMKB10Groups(
                    PreventionDTO(
                        pin = userPin,
                        cbicdsgIcd10SubGroupId = item.cbicdsgId
                    )
                )
                healthcareViewModel.getMKB10SubGroupsLiveData.observe(
                    lifecycleOwner, Observer { response ->
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                dataList = data
                            }
                            mKB10SubGroupsAdapter = MKB10SubGroupsAdapter(dataList)
                            holder.rvMKB10SubListSub.layoutManager = LinearLayoutManager(context)
                            holder.rvMKB10SubListSub.adapter = mKB10SubGroupsAdapter
                        } else {
                            println("Greška u odgovoru: ${response.message()}")
                        }
                    }
                )

                isClicked = true

            } else {
                holder.ivItemSub.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.ivItemSub.context,
                        R.drawable.ic_calendar_arrow_right
                    )
                )
                holder.clMKBSub.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.gray_E9EDFB
                    )
                )
                holder.rvMKB10SubListSub.visibility = View.GONE
                isClicked = false
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvItemSub: MaterialTextView = itemView.findViewById(R.id.mtvDescMKBSub)
        val mtvCodeMKBSub: MaterialTextView = itemView.findViewById(R.id.mtvCodeMKBSub)
        val ivItemSub: ImageView = itemView.findViewById(R.id.ivDescMKBSub)
        val clMKBSub: ConstraintLayout = itemView.findViewById(R.id.clMKBSub)
        val rvMKB10SubListSub: RecyclerView = itemView.findViewById(R.id.recyclerViewMKB10SubListSub)
    }

    fun updateData(newData: List<MKB10SubResponse>) {
        data = newData
        notifyDataSetChanged()
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