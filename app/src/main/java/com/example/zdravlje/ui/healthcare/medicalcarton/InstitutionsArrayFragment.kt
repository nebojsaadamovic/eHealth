package com.example.zdravlje.ui.healthcare.medicalcarton

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonRequest
import com.example.zdravlje.data.model.response.healthcare.Institution
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.InstitutionsArrAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentInstitutionsArrayBinding

class InstitutionsArrayFragment(
    private var className: String? = null
) : BaseFragment(), InstitutionsArrAdapter.OnInstitutionListener {

    private lateinit var binding: FragmentInstitutionsArrayBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    lateinit var institutionsArrAdapter: InstitutionsArrAdapter
    private var userPin: String? = null
    private var institutionalsArray: Array<Institution>? = null
    private var institutionCode: String? = ""
    var fieldAllInstitutions: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstitutionsArrayBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        fieldAllInstitutions = getString(R.string.fragment_institutions_array_all_institutions)
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
            MedicalCartonFragment.SharedCartonData.selectedCarton = "MedicalCarton"
            requireActivity().onBackPressed()
        }

        binding.mtvSave.setOnClickListener() {
            val medicalCartonFragment =
                MedicalCartonFragment(
                    className = "Institution"
                )

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.home_fragment_container_view,
                    medicalCartonFragment
                )
                .addToBackStack(null).commit()
        }
    }

    private fun observeToObservers() {
        userPin = getStoredPin()
        if (!userPin.isNullOrEmpty()) {
            setAdapter()
        }
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

    private fun setAdapter() {
        healthcareViewModel.getInstitutions(MedicalCartonRequest(pin = userPin))
        healthcareViewModel.getInstitutionsArrLiveData.observe(
            viewLifecycleOwner, Observer { response ->
                if (response.isSuccessful) {
                    showHideProgressBar(false)
                    val dataValue = response.body()
                    if (dataValue != null) {
                        val institutionList = dataValue.institutions.map { o ->
                            Institution(
                                institutionName = o.institutionName,
                                institutionCode = o.institutionCode
                            )
                        }

                        val allInstitutions = Institution(institutionName = fieldAllInstitutions)
                        institutionalsArray =
                            (listOf(allInstitutions) + institutionList).toTypedArray()

                        institutionsArrAdapter =
                            InstitutionsArrAdapter(
                                institutionalsArray!!,
                                parentFragmentManager,
                                className
                            )
                        institutionsArrAdapter.setOnInstitutionListener(this)

                        // 🔽 OVDE POSTAVI SELEKTOVANI ITEM nakon što adapter dobije podatke
                        val selectedCode = healthcareViewModel.institutionCode.value
                        selectedCode?.let { code ->
                            val selected = institutionalsArray?.find { it.institutionCode == code }
                            selected?.let { institution ->
                                institutionsArrAdapter.setSelectedInstitution(institution)
                            }
                        }

                        binding.reciclerViewInstitutions.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = institutionsArrAdapter
                            setHasFixedSize(true)
                        }
                    } else {
                        binding.clWrapperData.visibility = View.VISIBLE
                    }
                } else {
                    println("Greška u odgovoru: ${response.message()}")
                }
            })
    }

    private fun filterInstitutions(query: String) {
        val filteredList = institutionalsArray?.filter { institution ->
            institution.institutionName?.contains(query, ignoreCase = true) ?: true
        }
        if (filteredList != null) {
            updateRecyclerView(filteredList)
        }
    }

    private fun updateRecyclerView(filteredList: List<Institution>) {
        val filteredArray = filteredList.toTypedArray()
        institutionsArrAdapter.updateData(filteredArray)
    }

    override fun onInstitutionSelected(institution: String?, institutionName: String) {
        healthcareViewModel.institutionCode.value = institution
        className = "Institution"
    }

    private fun showHideProgressBar(boolean: Boolean) {
        if (boolean == true) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}