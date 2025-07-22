package com.example.zdravlje.ui.healthcare.prevention.commonsthemes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.CommonsThemesRequest
import com.example.zdravlje.data.model.response.healthcare.SocialGroup
import com.example.zdravlje.data.model.response.healthcare.SocialGroupMessage
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.SocialGroupsAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.nites.ezdravljeapp.databinding.FragmentCommonThemesBinding

class CommonsThemesFragment : BaseFragment(), PinBottomSheetFragment.OnPinListener {

    private lateinit var binding: FragmentCommonThemesBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var socialGroupsAdapter: SocialGroupsAdapter
    private var dataList: List<SocialGroup> ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommonThemesBinding.inflate(layoutInflater)
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
            healthcareViewModel.getCommonsThemes(CommonsThemesRequest())
        } else {
            val pinBottomSheetFragment = PinBottomSheetFragment()
            pinBottomSheetFragment.setOnPinListener(this)
            pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
        }

        healthcareViewModel.getCommonsThemesLiveData.observe(
            viewLifecycleOwner, Observer { response ->
                if (response.isSuccessful) {
                    showHideShimmer(false)
                    val dataValue = response.body()
                    if (dataValue != null) {
                        dataList = dataValue.flatMap { responseObject ->
                            responseObject.socialGroups?.map { group ->
                                SocialGroup(
                                    sgName = group.sgName,
                                    socialGroupMessages = group.socialGroupMessages?.map { message ->
                                        SocialGroupMessage(
                                            sgmId=message.sgmId
                                        )
                                })
                            } ?: emptyList()
                        }

                        socialGroupsAdapter = SocialGroupsAdapter(dataList!!, parentFragmentManager)
                        val linearLayoutManager = LinearLayoutManager(requireContext())
                        binding.reciclerViewCommonThemes.layoutManager = linearLayoutManager
                        binding.reciclerViewCommonThemes.adapter = socialGroupsAdapter
                    }
                } else {
                    println("Greška u odgovoru: ${response.message()}")
                }
            }
        )
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

    private fun filterInstitutions(query: String) {
        val filteredList = dataList?.filter { data ->
            data.sgName?.contains(query, ignoreCase = true) == true
        }
        if (filteredList != null) {
            updateRecyclerView(filteredList)
        }
    }

    private fun updateRecyclerView(filteredList: List<SocialGroup>) {
        val filteredArray = filteredList
        socialGroupsAdapter.updateData(filteredArray)
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    override fun onPinChange() {
        healthcareViewModel.getCommonsThemes(CommonsThemesRequest())
    }

    override fun onPinDismiss() {
        showHideShimmer(false)
        binding.clWrapperData.visibility = View.VISIBLE
        binding.reciclerViewCommonThemes.visibility = View.GONE
    }
}