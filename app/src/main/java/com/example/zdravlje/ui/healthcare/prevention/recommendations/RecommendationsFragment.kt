package com.example.zdravlje.ui.healthcare.prevention.recommendations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.PreventionDTO
import com.example.zdravlje.data.model.response.healthcare.RecommendationResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.RecommendationsAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentRecommendationsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecommendationsFragment : BaseFragment(), YearsBottomSheetFragment.OnYearSelectedListener,
    PinBottomSheetFragment.OnPinListener {

    private lateinit var binding: FragmentRecommendationsBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var recommendationsAdapter: RecommendationsAdapter
    private var dataList: List<RecommendationResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecommendationsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideShimmer(true)
        loadRecommendations()
        setupUI()
    }

    private fun setupUI() {
        binding.mtvOpen.setOnClickListener {
            val yearsBottomSheetFragment = YearsBottomSheetFragment()
            yearsBottomSheetFragment.setTargetFragment(this, 0)
            yearsBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                yearsBottomSheetFragment.tag
            )
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun loadRecommendations() {
        if (isPinSet()) {
            healthcareViewModel.getRecommendations(PreventionDTO(pin = getStoredPin(), limit = 1000))
        }
        else {
            val pinBottomSheetFragment = PinBottomSheetFragment()
            pinBottomSheetFragment.setOnPinListener(this)
            pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
        }

        healthcareViewModel.getRecommendationLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null && response.isSuccessful) {
                    showHideShimmer(false)
                    response.body()?.let { data ->
                        if (data.isNotEmpty()) {
                            dataList = data
                            updateAdapter(dataList)
                        }
                    }
                }
            })
    }

    private fun setAdapter(year: Int? = null) {
        if (year == null) {
            updateAdapter(dataList)
        } else {
            val filteredData = dataList.filter { item ->
                val itemYear = item.recDate?.let { convertTimestampToYear(it) }
                itemYear == year
            }

            if (filteredData.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_recommendations_no_data_for_this_year),
                    Toast.LENGTH_SHORT
                ).show()
            }

            updateAdapter(filteredData)
        }
    }

    private fun updateAdapter(filteredList: List<RecommendationResponse>) {
        recommendationsAdapter = RecommendationsAdapter(filteredList, parentFragmentManager)
        binding.recyclerViewRec.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRec.adapter = recommendationsAdapter
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

    fun convertTimestampToYear(timestamp: Long?): Int? {
        if (timestamp == null) return null
        val date = Date(timestamp * 1000)
        val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        return dateFormat.format(date).toIntOrNull()
    }

    override fun onYearSelected(value: String) {
        binding.mtvChooseYear.text = value
        val year = value.toIntOrNull()
        if (year != null) {
            setAdapter(year)
        }
        Toast.makeText(
            requireContext(),
            "${getString(R.string.fragment_recommendations_selected_year)}: $value",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    override fun onPinChange() {
        healthcareViewModel.getRecommendations(PreventionDTO(pin = getStoredPin(), limit = 1000))
    }

    override fun onPinDismiss() {
        showHideShimmer(false)
        binding.clWrapperData.visibility = View.VISIBLE
        binding.recyclerViewRec.visibility = View.GONE
    }
}
