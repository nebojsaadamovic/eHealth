package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.healthcare.ESickRequestDTO
import com.example.zdravlje.data.model.response.healthcare.SickLeaveNoteResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.healthcare.esickleave.OpenPdfFileUri2Fragment
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R

class EsickReportsAdapter(
    private var sickLeaveNotes: List<SickLeaveNoteResponse>? = null,
    private val fragmentManager: FragmentManager,
    private val userPin: String? = null,
    private val lifecycleOwner: LifecycleOwner,
    private val contextApp: Context,
) : RecyclerView.Adapter<EsickReportsAdapter.RecyclerViewHolder>() {

    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_esick_report, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        setViewModels()
        val item = sickLeaveNotes?.get(position)
        holder.mtvDateFromItemValue.text = item?.permanentWorkDisabilityFrom
        holder.mtvDateToItemValue.text = item?.permanentWorkDisabilityTo

        holder.clContentItem.setOnClickListener() {
                holder.progressBar.visibility = View.VISIBLE

                healthcareViewModel.getInabilityReport(
                    ESickRequestDTO(
                        urlForTempInabilityReport = item?.urlForTempInabilityReport,
                        pin = userPin
                    )
                )
                healthcareViewModel.inabilityReportLiveData.observe(
                    lifecycleOwner,
                    Observer { response ->
                        if (response.isSuccessful) {
                            holder.progressBar.visibility = View.GONE
                            val responseBody = response.body()

                            val contentType = response.headers()["Content-Type"]
                            println("Content-Type: $contentType")


                            if (responseBody != null) {
                                val fileName =
                                    holder.itemView.context.getString(R.string.adapter_report_disability_for_work_remittance_title)
                                val pdfBody = responseBody.bytes()

                                val fragment =
                                    OpenPdfFileUri2Fragment.newInstance(pdfBody, fileName)
                                fragmentManager.beginTransaction()
                                    ?.replace(R.id.home_fragment_container_view, fragment)
                                    ?.commit()
                            }
                        }
                    })

                healthcareViewModel.inabilityReportErrorLiveData.observe(
                    lifecycleOwner,
                    Observer { error ->
                        holder.progressBar.visibility = View.GONE
                        Toast.makeText(
                            holder.itemView.context,
                            "${holder.itemView.context.getString(R.string.error)}: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
    }

    override fun getItemCount(): Int {
        return sickLeaveNotes?.size ?: 0
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mtvDateFromItemValue: MaterialTextView =
            itemView.findViewById(R.id.mtvDateFromItemValue)
        val mtvDateToItemValue: MaterialTextView = itemView.findViewById(R.id.mtvDateToItemValue)
        val clContentItem: ConstraintLayout =
            itemView.findViewById(R.id.clContentItem)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBarEsick)
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(contextApp)
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel =
            ViewModelProvider(contextApp as ViewModelStoreOwner, healthcareViewModelFactory)
                .get(HealthcareViewModel::class.java)
    }
}
