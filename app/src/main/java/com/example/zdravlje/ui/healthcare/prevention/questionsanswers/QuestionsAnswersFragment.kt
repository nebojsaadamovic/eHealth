package com.example.zdravlje.ui.healthcare.prevention.questionsanswers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.healthcare.QAResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.ListQAAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentQuestionsAnswersBinding

class QuestionsAnswersFragment : BaseFragment(), PinBottomSheetFragment.OnPinListener {

    private lateinit var binding: FragmentQuestionsAnswersBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var listQAAdapter: ListQAAdapter
    private var allQAList: MutableList<QAResponse> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionsAnswersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideShimmer(true)
        setAdapter()
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        applyFilters()
    }

    private fun setupUI() {
        healthcareViewModel.healthInstitutionName?.let { binding.mtvChooseInstitution.text = it }
            ?: getString(R.string.fragment_prevention_select_institution)
        healthcareViewModel.healthTopicName?.let { binding.mtvChooseTopic.text = it }
            ?: getString(R.string.fragment_prevention_select_topic)
        healthcareViewModel.healthCategoryName?.let { binding.mtvChooseCategory.text = it }
            ?: getString(R.string.fragment_prevention_select_category)

        setupClickListeners()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        listQAAdapter = ListQAAdapter(emptyArray(), parentFragmentManager)
        binding.reciclerViewQA.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listQAAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.mtvChooseInstitution.setOnClickListener {
            openFragment(InstitutionCategoryTopicFragment("Institution"))
        }
        binding.mtvChooseCategory.setOnClickListener {
            openFragment(InstitutionCategoryTopicFragment("Category"))
        }
        binding.mtvChooseTopic.setOnClickListener {
            openFragment(InstitutionCategoryTopicFragment("Topic"))
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
            resetSelections()
            applyFilters()
        }
    }

    private fun openFragment(nameFragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container_view, nameFragment)
            .addToBackStack(null).commit()
    }

    private fun resetSelections() {
        healthcareViewModel.healthInstitutionName = null
        healthcareViewModel.healthCategoryName = null
        healthcareViewModel.healthTopicName = null
    }

    private fun setAdapter() {
        if (isPinSet()) {
            healthcareViewModel.listAllQA(getStoredPin())
        } else {
            val pinBottomSheetFragment = PinBottomSheetFragment()
            pinBottomSheetFragment.setOnPinListener(this)
            pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
        }

        healthcareViewModel.getAllQALiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    showHideShimmer(false)
                    val dataValue = response.body()
                    if (dataValue != null) {
                        allQAList.clear()
                        allQAList.addAll(dataValue.map { o ->
                            QAResponse(
                                citengInstitutionName = o.citengInstitutionName,
                                category = o.category,
                                citengQuestion = o.citengQuestion,
                                citengAnswered = o.citengAnswer,
                                citengGenerated = o.citengGenerated,
                                topic = o.topic
                            )
                        })
                        applyFilters()
                    } else {
                        println("No data in response")
                    }
                } else {
                    println("Error: ${response.message()} Status Code: ${response.code()}")
                }
            })
    }

    private fun applyFilters() {
        val institutionName = healthcareViewModel.healthInstitutionName
        val categoryName = healthcareViewModel.healthCategoryName
        val topicName = healthcareViewModel.healthTopicName

        val filteredList = allQAList.filter { item ->
            val institutionMatches = institutionName?.let {
                it != getString(R.string.fragment_prevention_select_institution) && item.citengInstitutionName == it
            } ?: true

            val categoryMatches = categoryName?.let {
                it != getString(R.string.fragment_prevention_select_category) && item.category == it
            } ?: true

            val topicMatches = topicName?.let {
                it != getString(R.string.fragment_prevention_select_topic) && item.topic == it
            } ?: true

            institutionMatches && categoryMatches && topicMatches
        }

        updateAdapter(filteredList)
    }

    private fun updateAdapter(filteredList: List<QAResponse>) {
        listQAAdapter.updateData(filteredList.toTypedArray())
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory)
            .get(HealthcareViewModel::class.java)
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    override fun onPinChange() {
        healthcareViewModel.listAllQA(getStoredPin())
    }

    override fun onPinDismiss() {
        showHideShimmer(false)
        binding.clWrapperData.visibility = View.VISIBLE
        binding.reciclerViewQA.visibility = View.GONE
    }
}