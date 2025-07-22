package com.example.zdravlje.ui.healthcare.medicalcarton

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonRequest
import com.example.zdravlje.data.model.response.healthcare.AccessMedicalCartonResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.AccesMedicalCartonAdapter
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.databinding.FragmentAccessMedicalCartonBinding

class AccessMedicalCartonFragment(
    private var userPin: String? = null
) : Fragment() {

    private lateinit var binding: FragmentAccessMedicalCartonBinding
    private lateinit var accesMedicalCartonAdapter: AccesMedicalCartonAdapter
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var healthcareViewModel: HealthcareViewModel
    private var accesMeddicalCartonArray: Array<AccessMedicalCartonResponse>? = null
    var institutionList: List<AccessMedicalCartonResponse>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccessMedicalCartonBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideProgressBar(true)
        setAdapter()
        setupUI()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    private fun setAdapter() {
        healthcareViewModel.getAccessMedicalCarton(
            MedicalCartonRequest(
                pin = userPin,
            )
        )

        healthcareViewModel.getAccessMedicalCartonLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                showHideProgressBar(false)
                try {
                    response.body()?.let { dataValue ->
                        institutionList = dataValue.map { item ->
                            AccessMedicalCartonResponse(
                                institutionCode = item.institutionCode,
                                institutionName = item.institutionName,
                                rfzoDoctorID = item.rfzoDoctorID,
                                rfzoDoctorLBO = item.rfzoDoctorLBO,
                                doctorFullName = item.doctorFullName,
                                createDate = item.createDate,
                                npi = item.npi
                            )
                        }

                        if (institutionList?.isNotEmpty() == true) {
                            accesMeddicalCartonArray = institutionList!!.toTypedArray()
                            accesMedicalCartonAdapter = AccesMedicalCartonAdapter(
                                accesMeddicalCartonArray!!,
                                parentFragmentManager
                            )

                            val linearLayoutManager = object : LinearLayoutManager(
                                requireContext(), LinearLayoutManager.VERTICAL, false
                            ) {
                                override fun canScrollVertically() = true
                            }

                            binding.reciclerViewAccessMedicalCarton.layoutManager =
                                linearLayoutManager
                            binding.reciclerViewAccessMedicalCarton.adapter =
                                accesMedicalCartonAdapter
                        } else {
                            binding.clWrapperData.visibility = View.VISIBLE
                        }
                    } ?: run {
                        Log.e("HealthcareViewModel", "⚠️ Response body is null")
                    }
                } catch (e: Exception) {
                    Log.e("HealthcareViewModel", "❌ Greška prilikom mapiranja: ${e.message}", e)
                }
            } else {
                val errorMsg = response.errorBody()?.string()
                Log.e("HealthcareViewModel", "❌ API call failed: $errorMsg")
            }
        }

        healthcareViewModel.getAccessMedicalCartonErrorLiveData.observe(viewLifecycleOwner) { response ->
            showHideProgressBar(false)
        }
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(
            requireActivity(),
            healthcareViewModelFactory
        ).get(HealthcareViewModel::class.java)
    }

    private fun showHideProgressBar(boolean: Boolean){
        if(boolean==true){
            binding.progressBarr.visibility = View.VISIBLE
        }else{
            binding.progressBarr.visibility = View.GONE
        }
    }
}