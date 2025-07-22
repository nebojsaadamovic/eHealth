package com.example.zdravlje.ui.healthcare.medicalcarton

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonRequest
import com.example.zdravlje.data.model.response.healthcare.System
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.InstitutionsAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.databinding.FragmentInstitutionsBinding

class InstitutionsFragment : BaseFragment() {

    private lateinit var binding: FragmentInstitutionsBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    lateinit var institutionsAdapter: InstitutionsAdapter
    private var userPin: String? = null
    private var institutionalsArray: Array<System>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstitutionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideProgressBar(true)
        observeToObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.mtvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                filterInstitutions(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    private fun observeToObservers() {

        userPin=getStoredPin()
        if (!userPin.isNullOrEmpty()) {
            setAdapter()
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

    private fun setAdapter() {
        healthcareViewModel.getListInstitutions(MedicalCartonRequest(pin = userPin))
        healthcareViewModel.getInstitutionsLiveData.observe(
            viewLifecycleOwner, Observer { response ->
                if (response.isSuccessful) {
                    showHideProgressBar(false)
                    val dataValue = response.body()
                    if (dataValue != null) {
                        binding.mtvRelatedAvailableInstitutionsValue.text =
                            dataValue.data.online.toString()
                        binding.mtvAddedInstitutionsValue.text =
                            dataValue.data.online_new.toString()

                        val institutionList = dataValue.data.systems

                        if (!institutionList.isNotEmpty()) {
                            binding.clWrapperData.visibility = View.VISIBLE
                        } else {
                            institutionalsArray = institutionList.toTypedArray()
                            institutionsAdapter = InstitutionsAdapter(
                                institutionalsArray!!,
                                parentFragmentManager,
                                viewLifecycleOwner,
                                requireContext(),
                                userPin
                            )
                            val linearLayoutManager = object : LinearLayoutManager(
                                requireContext(), LinearLayoutManager.VERTICAL, false
                            ) {
                                override fun canScrollVertically() = true
                            }
                            binding.reciclerViewInstitutions.layoutManager = linearLayoutManager
                            binding.reciclerViewInstitutions.adapter = institutionsAdapter
                        }
                    }
                } else {
                    println("Greška u odgovoru: ${response.message()}")
                }
            })
    }

    private fun filterInstitutions(query: String) {
        val filteredList = institutionalsArray?.filter { institution ->
            institution.name.contains(query, ignoreCase = true)
        }
        if (filteredList != null) {
            updateRecyclerView(filteredList)
        }
    }

    private fun updateRecyclerView(filteredList: List<System>) {
        val filteredArray = filteredList.toTypedArray()
        institutionsAdapter.updateData(filteredArray)
    }

    private fun showHideProgressBar(boolean: Boolean){
        if(boolean==true){
            binding.progressBarr.visibility = View.VISIBLE
        }else{
            binding.progressBarr.visibility = View.GONE
        }
    }
}