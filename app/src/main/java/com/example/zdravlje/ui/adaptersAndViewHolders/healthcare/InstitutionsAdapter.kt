package com.example.zdravlje.ui.adaptersAndViewHolders.healthcare

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonRequest
import com.example.zdravlje.data.model.response.healthcare.System
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.healthcare.medicalcarton.InstitutionsFragment
import com.nites.ezdravljeapp.R

class InstitutionsAdapter(
    private var institutionArray: Array<System>,
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner,
    private var context: Context,
    private var userPin: String? = null
) : RecyclerView.Adapter<InstitutionsAdapter.RecyclerViewHolder>() {

    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InstitutionsAdapter.RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institution, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstitutionsAdapter.RecyclerViewHolder, position: Int) {
        setViewModels()
        val item = institutionArray[position]
        holder.textViewInstitutions.text = item.name
        if (item.health.equals("online", ignoreCase = true)) {
            holder.imageViewStatus.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.green_5CB85C
                )
            )
        } else {
            holder.imageViewStatus.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.red_FFE50915
                )
            )
        }
        holder.imageViewRefresh.setOnClickListener() {
            healthcareViewModel.refreshInstitutionData(
                MedicalCartonRequest(
                    pin = userPin,
                    institutionId = item.internalId
                )
            )
            healthcareViewModel.getRefreshInstitutionDataLiveData.observeOnce(
                lifecycleOwner, Observer { response ->
                    if (response.isSuccessful) {
                        val data = response.body()
                        val messageRes = if (data == "false") {
                            R.string.institutions_adapter_msg_one
                        } else {
                            R.string.institutions_adapter_msg_two
                        }

                        val dialog = AlertDialog.Builder(context)
                            .setMessage(holder.itemView.context.getString(messageRes))
                            .setCancelable(false)
                            .create()

                        dialog.show()

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (dialog.isShowing) {
                                dialog.dismiss()
                            }
                        }, 2000)
                    }
                }
            )

            holder.progressBar.visibility = View.VISIBLE
            val fragment =
                fragmentManager.findFragmentByTag(InstitutionsFragment::class.java.simpleName)
            if (fragment != null) {
                fragmentManager.beginTransaction()
                    .detach(fragment)
                    .attach(fragment)
                    .commit()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                holder.progressBar.visibility = View.GONE
            }, 1000)
        }
    }

    override fun getItemCount(): Int {
        return institutionArray.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewInstitutions: TextView = itemView.findViewById(R.id.mtvInstitutions)
        val imageViewStatus: ImageView = itemView.findViewById(R.id.ivStatus)
        val imageViewRefresh: ImageView = itemView.findViewById(R.id.ivRefresh)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBarr)
    }

    fun updateData(newData: Array<System>) {
        institutionArray = newData
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

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }
}