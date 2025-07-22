package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.healthcare.MKB10Response
import com.example.zdravlje.data.model.request.healthcare.MKB10SubResponse
import com.example.zdravlje.data.model.request.healthcare.PreventionDTO
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.R

class MKB10Adapter(
    private var data: List<MKB10Response>,
    private var userPin: String,
    private val fragmentManager: FragmentManager,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<MKB10Adapter.RecyclerViewHolder>() {

    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var mKB10SubAdapter: MKB10SubAdapter
    private var dataList: List<MKB10SubResponse> = emptyList()

    var isClicked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mkb10, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = data[position]
        setViewModels()
        holder.mtvItem.text = item.cbicdgNameSh
        holder.mtvCodeMKB.text = item.cbicdgCode

        holder.ivItem.setOnClickListener {
            if (!isClicked) {
                holder.ivItem.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.ivItem.context,
                        R.drawable.ic_arrow_bottom
                    )
                )
                holder.clMKB.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                holder.rvMKB10SubList.visibility = View.VISIBLE

                healthcareViewModel.getSubMKB10(
                    PreventionDTO(
                        pin = userPin,
                        cbicdsgIcd10groupId = item.cbicdgId
                    )
                )
                healthcareViewModel.getMKB10SubLiveData.observe(
                    lifecycleOwner, Observer { response ->
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                dataList = data
                            }
                            mKB10SubAdapter = MKB10SubAdapter(
                                dataList,
                                userPin,
                                fragmentManager,
                                context,
                                lifecycleOwner
                            )
                            holder.rvMKB10SubList.layoutManager = LinearLayoutManager(context)
                            holder.rvMKB10SubList.adapter = mKB10SubAdapter
                        } else {
                            println("Greška u odgovoru: ${response.message()}")
                        }
                    }
                )

                isClicked = true

            } else {
                holder.ivItem.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.ivItem.context,
                        R.drawable.ic_calendar_arrow_right
                    )
                )
                holder.clMKB.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.gray_E9EDFB
                    )
                )
                holder.rvMKB10SubList.visibility = View.GONE
                isClicked = false
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvItem: TextView = itemView.findViewById(R.id.mtvDescMKB)
        val mtvCodeMKB: TextView = itemView.findViewById(R.id.mtvCodeMKB)
        val ivItem: ImageView = itemView.findViewById(R.id.ivDescMKB)
        val clMKB: ConstraintLayout = itemView.findViewById(R.id.clMKB)
        val rvMKB10SubList: RecyclerView = itemView.findViewById(R.id.recyclerViewMKB10SubList)
    }

    fun updateData(newData: List<MKB10Response>) {
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