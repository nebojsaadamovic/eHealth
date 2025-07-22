package com.example.zdravlje.ui.healthcare.prevention.interactions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.healthcare.InteractionMedicineResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.MedicamenteInteractionAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.databinding.FragmentMedicamenteInteractionBinding

class MedicamenteInteractionFragment : BaseFragment() {

    private lateinit var binding: FragmentMedicamenteInteractionBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var medicamenteInteractionAdapter: MedicamenteInteractionAdapter
    var data: List<InteractionMedicineResponse> ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMedicamenteInteractionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

   private fun setupUI(){
       healthcareViewModel.getAllMedicines()

       binding.mtvSearch.addTextChangedListener(object : TextWatcher {
           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               val query = s.toString()
               if (query.length >=1 && query.length  < 3) {
                   binding.clWrapperData.visibility = View.GONE
                   setAdapter(emptyList())
               }
               else if (query.length < 1){
                   binding.clWrapperData.visibility = View.GONE
                   setAdapter(data!!)
               }
               else {
                   filterInstitutions(query)
               }
           }

           override fun afterTextChanged(s: Editable?) {}
       })
   }

    private fun filterInstitutions(query: String) {
        val filteredList = data?.filter { item ->
            item.name.contains(query, ignoreCase = true)
        }

        if (!filteredList.isNullOrEmpty()) {
            binding.clWrapperData.visibility = View.GONE
            medicamenteInteractionAdapter.updateData(filteredList)
        }else{
            binding.clWrapperData.visibility = View.VISIBLE
        }
    }

    private fun subscribeToObservers() {
        healthcareViewModel.getIteractionMedicineLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                data = response.body()
                if (data != null) {
                    setAdapter(data!!)
                }else{

                }
            }else{

            }
        }
    }

    private fun setAdapter(items : List<InteractionMedicineResponse>) {
        medicamenteInteractionAdapter = MedicamenteInteractionAdapter(items, parentFragmentManager, requireContext())
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.rvInteractionsMedicamente.layoutManager = linearLayoutManager
        binding.rvInteractionsMedicamente.adapter = medicamenteInteractionAdapter
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory)
            .get(HealthcareViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        healthcareViewModel.getAllMedicines()
    }
}