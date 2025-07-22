package com.example.zdravlje.ui.healthcare.medicalcarton

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMedicalCartonOptionsBinding

class MedicalCartonOptionsFragment : BaseFragment() {

    private lateinit var binding:FragmentMedicalCartonOptionsBinding
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var healthcareViewModel: HealthcareViewModel
    private var userPin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
       binding=FragmentMedicalCartonOptionsBinding.inflate(layoutInflater)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        observeToObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.clInstitution.setOnClickListener() {
            val institutionsFragment = InstitutionsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, institutionsFragment)
                .addToBackStack(null).commit()
        }

        binding.clECarton.setOnClickListener() {
            val accessMedicalCartonFragment = AccessMedicalCartonFragment(userPin)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, accessMedicalCartonFragment)
                .addToBackStack(null).commit()
        }

        binding.blurBackground.setOnClickListener(){
            MedicalCartonFragment.SharedCartonData.selectedCarton = "MedicalCarton"
            requireActivity().onBackPressed()
        }
    }

    private fun observeToObservers(){
        userPin=getStoredPin()
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory = HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory).get(
            HealthcareViewModel::class.java)
    }
}