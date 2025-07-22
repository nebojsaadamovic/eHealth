package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.healthcare.ESickRequestDTO
import com.example.zdravlje.data.model.response.healthcare.CommissionDecision
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.healthcare.esickleave.OpenPdfFileUri2Fragment
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R

data class ReportDisabilityForWorkAdapter(
    private var dataArr: List<CommissionDecision>,
    private var parentFragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val userPin: String?,
    private var data: SickLeaveResponse? = null
) : RecyclerView.Adapter<ReportDisabilityForWorkAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_disability_work, parent, false)
        return RecyclerViewHolder(view)
    }

    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onBindViewHolder(
        holder: ReportDisabilityForWorkAdapter.RecyclerViewHolder,
        position: Int
    ) {
        setViewModels()
        val item = dataArr[position]
        holder.tvStartDate.text = DateUtils.convertYYYYMMDDHHssToDotTDDMMYYYY(item.dateOfDecision)
        holder.tvEndDate.text = item.numberOfDecision

        holder.tvRemittance.setOnClickListener() {
            val context = holder.itemView.context
            val sickNoteId = item.commissionDecisionId
            if (sickNoteId == null) {
                Toast.makeText(
                    context,
                    holder.itemView.context.getString(R.string.report_disability_for_work_adapter_id_toast),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val request = ESickRequestDTO(
                sickNoteId = sickNoteId,
                pin = userPin,
                urlForCommissionDecision = item.urlForCommissionDecision
            )
            healthcareViewModel.getDownloadCommisionDecission(request)
            healthcareViewModel.getSickReportLiveData.observe(lifecycleOwner, Observer { response ->
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val fileName =
                            holder.itemView.context.getString(R.string.item_e_sick_leave_temporary_work_incapacity_report)

                        val pdfBody = responseBody.bytes()

                        val fragment =
                            OpenPdfFileUri2Fragment.newInstance(pdfBody, fileName)
                        parentFragmentManager.beginTransaction()
                            ?.replace(R.id.home_fragment_container_view, fragment)
                            ?.commit()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "${holder.itemView.context.getString(R.string.report_disability_for_work_adapter_download_rejected)} ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            healthcareViewModel.getSickReportErrorLiveData.observe(
                lifecycleOwner,
                Observer { error ->
                    Toast.makeText(
                        context,
                        "${holder.itemView.context.getString(R.string.error)} ${
                            error ?: holder.itemView.context.getString(
                                R.string.report_disability_for_work_adapter_unknown_error
                            )
                        }",
                        Toast.LENGTH_SHORT
                    ).show()

                })
        }
    }

    override fun getItemCount(): Int {
        return dataArr.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStartDate: TextView = itemView.findViewById(R.id.mtvStartDateItemValue)
        val tvEndDate: TextView = itemView.findViewById(R.id.mtvEndDateItemValue)
        val tvRemittance: TextView = itemView.findViewById(R.id.mtvRemittanceItemValue)
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