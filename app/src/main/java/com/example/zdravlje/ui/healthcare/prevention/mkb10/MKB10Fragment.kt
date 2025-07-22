package com.example.zdravlje.ui.healthcare.prevention.mkb10

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.healthcare.MKB10Response
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.MKB10Adapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.nites.ezdravljeapp.databinding.FragmentMKB10Binding

class MKB10Fragment : BaseFragment(), PinBottomSheetFragment.OnPinListener {

    private lateinit var binding: FragmentMKB10Binding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var mKB10Adapter: MKB10Adapter
    private var dataList: List<MKB10Response> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMKB10Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideShimmer(true)
        setViewModels()
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
            healthcareViewModel.getMKB10(PinRequest(pin = getStoredPin()))
        } else {
            val pinBottomSheetFragment = PinBottomSheetFragment()
            pinBottomSheetFragment.setOnPinListener(this)
            pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
        }

        healthcareViewModel.getMKB10LiveData.observe(
            viewLifecycleOwner, Observer { response ->
                println(response)
                if (response.isSuccessful) {
                    showHideShimmer(false)
                    val data = response.body()
                    if (data != null) {
                        dataList = data
                    }
                    mKB10Adapter = MKB10Adapter(
                        dataList,
                        getStoredPin(),
                        parentFragmentManager,
                        requireContext(),
                        viewLifecycleOwner
                    )
                    binding.recyclerViewMKB10.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerViewMKB10.adapter = mKB10Adapter
                } else {
                    showHideShimmer(false)
                    println("Greška u odgovoru: ${response.message()}")
                }
            }
        )
    }

    private fun filterInstitutions(query: String) {
        val filteredList = dataList.filter { data ->
            data.cbicdgNameSh?.contains(query, ignoreCase = true) == true
        }

        mKB10Adapter.updateData(filteredList)
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

    override fun onPinChange() {
        healthcareViewModel.getMKB10(PinRequest(pin = getStoredPin()))
    }

    override fun onPinDismiss() {
        showHideShimmer(false)
        binding.clWrapperData.visibility = View.VISIBLE
        binding.recyclerViewMKB10.visibility = View.GONE
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }
}