package com.example.zdravlje.ui.healthcare.prevention.interactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.google.android.material.tabs.TabLayout
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentInteractionBinding

class InteractionFragment : BaseFragment() {

    private lateinit var binding: FragmentInteractionBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var interactionsSwitchPage: InteractionsSwitchPage
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInteractionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setSwitchPager()
        setTabLayout();
        subscribeObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.ivFilter.setOnClickListener() {
            val bottomSheet = BottomSheetFilterInteractionsFragment()
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }

        binding.ivCompareMedicamente.setOnClickListener() {
            CommonUtils.openFragmentHome(requireActivity(), InteractionsCheckFragment())
        }

        binding.ivBack.setOnClickListener() {
            healthcareViewModel.listNameInn.value = null
            requireActivity().onBackPressed()
        }
    }

    private fun setTabLayout() {
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)!!.setText(getString(R.string.fragment_interaction_tab_first))
        tabLayout.getTabAt(1)!!.setText(getString(R.string.fragment_interaction_tab_second))
    }

    private fun setSwitchPager() {
        interactionsSwitchPage = InteractionsSwitchPage(parentFragmentManager)
        viewPager.adapter = interactionsSwitchPage
    }

    private fun subscribeObservers() {
        healthcareViewModel.listNameInn.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                binding.ivCompareMedicamente.setImageResource(R.drawable.ic_count_compare_medicamente)
                binding.mtvCount.visibility = View.VISIBLE
                binding.mtvCount.text = response.size.toString()
            } else {
                binding.ivCompareMedicamente.setImageResource(R.drawable.ic_compare_medicamente)
                binding.mtvCount.visibility = View.GONE
            }
        }
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

    override fun onResume() {
        super.onResume()
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setSwitchPager()
        setTabLayout();
        subscribeObservers()
        setupUI()
    }
}