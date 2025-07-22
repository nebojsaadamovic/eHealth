package com.example.zdravlje.ui.calendar.medicament.withrecipe

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.RecipeRequest
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.calendar.MedicineAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMedicamentWithReceptBinding

class MedicamentWithRecipeFragment : BaseFragment() {

    lateinit var binding: FragmentMedicamentWithReceptBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var medicineAdapter: MedicineAdapter
    private var medicineList: List<PrescribedListMedicamentResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMedicamentWithReceptBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideShimmer(true)
        subscribeToObservers()
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

    private fun setViewModels() {
        val calendarRepository = CalendarRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val calendarViewModelFactory =
            CalendarViewModelFactory(calendarRepository, dataStoreRepository)

        calendarViewModel = ViewModelProvider(
            requireActivity(),
            calendarViewModelFactory
        ).get(CalendarViewModel::class.java)
    }

    private fun subscribeToObservers() {
        getMedicament()

        calendarViewModel.getPatienteMedicamenteLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                showHideShimmer(false)
                medicineList = response.body()!!
                if (medicineList.isNullOrEmpty()) {
                    binding.clWrapperData.visibility = View.VISIBLE
                } else {
                    binding.recyclerViewMedicines.layoutManager = LinearLayoutManager(requireContext())
                    medicineAdapter = MedicineAdapter(medicineList!!)
                    binding.recyclerViewMedicines.adapter = medicineAdapter
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        calendarViewModel.getMedicineErrorLiveData.observe(viewLifecycleOwner) { response ->
            showHideShimmer(false)
            Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMedicament() {
        println("sssssssssss1111111wwwwwww")
        calendarViewModel.searchPatientMedicine(RecipeRequest(pin = getStoredPin(), limitIn = 1000))
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    private fun filterInstitutions(query: String) {
        val filteredList = medicineList?.filter { data ->
            data.medicamentProtectedName?.contains(query, ignoreCase = true) == true
        }
        if (filteredList != null) {
            medicineAdapter.updateData(filteredList)
        }
    }
}