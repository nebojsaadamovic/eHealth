package com.example.zdravlje.ui.healthcare.prevention.interactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.healthcare.MedicineCompareItem
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.InteractionsCheckAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.nites.ezdravljeapp.databinding.FragmentInteractionsCheckBinding

class InteractionsCheckFragment : BaseFragment() {

    private lateinit var binding: FragmentInteractionsCheckBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var interactionsCheckAdapter: InteractionsCheckAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInteractionsCheckBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.mbCheck.setOnClickListener {
            CommonUtils.openFragmentHome(requireActivity(), InteractionsDescriptionFragment())
        }

        binding.ivInfo.setOnClickListener {
            val bottomSheet = BottomSheetTypeInteractionsFragment()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }

        binding.mbClear.setOnClickListener {
            val currentList = healthcareViewModel.listNameInn.value?.toMutableList()
            healthcareViewModel.listNameInn.value?.let { it1 -> currentList?.removeAll(it1) }
            healthcareViewModel.listNameInn.value = currentList
        }
    }

    private fun subscribeToObservers() {
        healthcareViewModel.listNameInn.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                setAdapter(data)
            }
        }
    }

    private fun setAdapter(items: List<MedicineCompareItem>) {
        interactionsCheckAdapter =
            InteractionsCheckAdapter(items, parentFragmentManager, requireContext())
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.rvInteractionsMedicamente.layoutManager = linearLayoutManager
        binding.rvInteractionsMedicamente.adapter = interactionsCheckAdapter
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(
            requireActivity(), healthcareViewModelFactory
        ).get(HealthcareViewModel::class.java)
    }
}