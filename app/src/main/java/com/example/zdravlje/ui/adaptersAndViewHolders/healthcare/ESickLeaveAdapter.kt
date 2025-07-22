package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.healthcare.esickleave.ESickDocumentsFragment
import com.example.zdravlje.ui.healthcare.esickleave.ReportDisabilityForWorkFragment
import com.nites.ezdravljeapp.R

class ESickLeaveAdapter(
    private var eSickLeaveArr: Array<SickLeaveResponse>,
    private var parentFragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val userPin: String?
) : RecyclerView.Adapter<ESickLeaveAdapter.RecyclerViewHolder>() {

    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_e_sick_leave, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        setViewModels()
        val item = eSickLeaveArr[position]
        holder.tvSickLeaveStatus.text = item.sickManagementStatus
        holder.tvStartDate.text = item.startDate
        holder.tvEndDate.text = item.endDate
        holder.tvNumberOfDays.text = item.numberOfSickDays.toString()
        holder.tvReasonForSickLeave.text = item.sickReason
        holder.tvDiagnosisCodeAndDescription.text = item.diagnoses.firstOrNull()?.name

        holder.tvTemporaryWorkIncapacityCertificate.setOnClickListener {
           holder.progressBar.visibility= View.VISIBLE
            val context = holder.itemView.context
            val employers = item.employers
            if (employers.isNotEmpty()) {
                val fragment = ESickDocumentsFragment(item, userPin = userPin)
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.home_fragment_container_view, fragment)
                    addToBackStack(null)
                    commit()
                }
            }else{
                Toast.makeText(
                    context,
                    holder.itemView.context.getString(R.string.esick_leave_adapter_url_toast),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
        }

        holder.tvTemporaryWorkIncapacityReport.setOnClickListener() {
            val reportDisableWorkFragment = ReportDisabilityForWorkFragment(item, userPin)
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.home_fragment_container_view, reportDisableWorkFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return eSickLeaveArr.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSickLeaveStatus: TextView = itemView.findViewById(R.id.mtvSickLeaveStatusItemValue)
        val tvStartDate: TextView = itemView.findViewById(R.id.mtvStartDateItemValue)
        val tvEndDate: TextView = itemView.findViewById(R.id.mtvEndDateItemValue)
        val tvNumberOfDays: TextView = itemView.findViewById(R.id.mtvNumberOfDaysItemValue)
        val tvReasonForSickLeave: TextView =
            itemView.findViewById(R.id.mtvReasonForSickLeaveItemValue)
        val tvDiagnosisCodeAndDescription: TextView =
            itemView.findViewById(R.id.mtvDiagnosisCodeAndDescriptionItemValue)
        val tvTemporaryWorkIncapacityCertificate: TextView =
            itemView.findViewById(R.id.mtvTemporaryWorkIncapacityCertificateItemValue)
        val tvTemporaryWorkIncapacityReport: TextView =
            itemView.findViewById(R.id.mtvTemporaryWorkIncapacityReportItemValue)
        val progressBar: ProgressBar =
            itemView.findViewById(R.id.progressBar)
    }

    fun updateData(newData: Array<SickLeaveResponse>) {
        eSickLeaveArr = newData
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
