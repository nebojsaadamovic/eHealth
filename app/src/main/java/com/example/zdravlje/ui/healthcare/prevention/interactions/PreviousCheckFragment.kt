package com.example.zdravlje.ui.healthcare.prevention.interactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.databinding.FragmentPreviousCheckBinding

class PreviousCheckFragment : Fragment() {

    private lateinit var binding: FragmentPreviousCheckBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviousCheckBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
//        subscribeToObservers()
//        setupUI()
    }

//    private fun setupUI() {
//        healthcareViewModel.getStaticRag(
//            patientId = 20,
//            StaticRagRequest(
//                query = listOf(
//                    QueryItem(name = "abakavir", type = "INN"),
//                    QueryItem(name = "abemaciklib", type = "INN")
//                )
//            )
//        )
//    }
//
//
//
//    private fun subscribeToObservers() {
//        healthcareViewModel.getStaticRagLiveData.observe(viewLifecycleOwner) { response ->
//            if (response.isSuccessful) {
//                val data = response.body()
//                if (data != null) {
//                    //setAdapter(data.note)
//                }else{
//
//                }
//            }else{
//
//            }
//        }
//    }




//    private fun setAdapter(items : NoteStaticRag) {
//        compareAdapter = CompareAdapter(items, parentFragmentManager)
//        val linearLayoutManager =
//            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
//                override fun canScrollVertically() = true
//            }
//        binding.rvInteracitonsMedicamentes.layoutManager = linearLayoutManager
//        binding.rvInteracitonsMedicamentes.adapter = compareAdapter
//    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory)
            .get(HealthcareViewModel::class.java)
    }

}