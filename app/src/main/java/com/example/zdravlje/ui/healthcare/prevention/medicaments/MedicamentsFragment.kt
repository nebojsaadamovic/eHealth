package com.example.zdravlje.ui.healthcare.prevention.medicaments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.PreventionDTO
import com.example.zdravlje.data.model.response.healthcare.MedicamenteResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.MedicamenteAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.nites.ezdravljeapp.databinding.FragmentMedicamentsBinding

class MedicamentsFragment : BaseFragment(), PinBottomSheetFragment.OnPinListener {

    private lateinit var binding: FragmentMedicamentsBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var medicamenteAdapter: MedicamenteAdapter
    private var dataList: List<MedicamenteResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMedicamentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideShimmer(true)
        setAdapter()
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

    private fun setAdapter() {
        if (isPinSet()) {
            healthcareViewModel.getMedicaments(PreventionDTO(character = "a", pin = getStoredPin()))
        } else {
            val pinBottomSheetFragment = PinBottomSheetFragment()
            pinBottomSheetFragment.setOnPinListener(this)
            pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
        }

        healthcareViewModel.getMedicamenteLiveData.observe(
            viewLifecycleOwner, Observer { response ->
                println(response)
                if (response.isSuccessful) {
                    showHideShimmer(false)
                    val data = response.body()
                    if (data != null) {
                        dataList = data
                    }
                    medicamenteAdapter = MedicamenteAdapter(dataList, parentFragmentManager)
                    binding.recyclerViewMedicamente.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.recyclerViewMedicamente.adapter = medicamenteAdapter
                } else {
                    println("Greška u odgovoru: ${response.message()}")
                }
            }
        )
    }

    private fun filterInstitutions(query: String) {
        val filteredList = dataList.filter { data ->
            data.cbmedProtectedName?.contains(query, ignoreCase = true) == true
        }
        medicamenteAdapter.updateData(filteredList)
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory).get(
            HealthcareViewModel::class.java
        )
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    override fun onPinChange() {
        healthcareViewModel.getMedicaments(PreventionDTO(character = "a", pin = getStoredPin()))
    }

    override fun onPinDismiss() {
        showHideShimmer(false)
        binding.clWrapperData.visibility = View.VISIBLE
        binding.recyclerViewMedicamente.visibility = View.GONE
    }
}