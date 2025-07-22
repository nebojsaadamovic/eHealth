package com.example.zdravlje.ui.healthcare.prevention.questionsanswers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.healthcare.CategoryQAResponse
import com.example.zdravlje.data.model.response.healthcare.Institution
import com.example.zdravlje.data.model.response.healthcare.TopicQAResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.CategoryQAAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.InstitutionsArrAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.TopicQAAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentInstitutionCategoryTopicBinding

class InstitutionCategoryTopicFragment(
    private var className: String? = null
) : BaseFragment(),
    InstitutionsArrAdapter.OnInstitutionListener, CategoryQAAdapter.OnCategoryQAListener,
    TopicQAAdapter.OnTopicQAListener {
    private lateinit var binding: FragmentInstitutionCategoryTopicBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var institutionsArrAdapter: InstitutionsArrAdapter
    private lateinit var categoryQAAdapter: CategoryQAAdapter
    private lateinit var topicQAAdapter: TopicQAAdapter

    private var institutionalsArray: Array<Institution>? = null
    private var categoryArray: Array<CategoryQAResponse>? = null
    private var topicArray: Array<TopicQAResponse>? = null
    private var userPin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstitutionCategoryTopicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        observeToObservers()
        setupUI()
    }

    private fun setupUI() {
        if (className == "Category") {
            binding.mtvTitle.text = getString(R.string.fragment_institution_category_topic_category)
        }
        if (className == "Topic") {
            binding.mtvTitle.text = getString(R.string.fragment_institution_category_topic_topic)
        }

        binding.mtvChoose.setOnClickListener() {
            openFragment(QuestionsAnswersFragment())
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.mtvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                filterInstitutions(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeToObservers() {
        if (isPinSet()){
            userPin=getStoredPin()
            setAdapter()

        }
    }

    private fun setAdapter() {
        if (className == "Institution") {
            healthcareViewModel.getHealthInstitutionQA(userPin!!)
            healthcareViewModel.getHealthInstitutionLiveData.observe(
                viewLifecycleOwner,
                Observer { response ->
                    if (response.isSuccessful) {
                        val dataValue = response.body()
                        if (dataValue != null) {
                            val institutionList = dataValue.map { o ->
                                Institution(
                                    registrationNumber = o.registrationNumber,
                                    name = o.name
                                )
                            }
                            institutionalsArray = institutionList.toTypedArray()
                            institutionsArrAdapter =
                                InstitutionsArrAdapter(
                                    institutionalsArray!!,
                                    parentFragmentManager,
                                    className
                                )
                            institutionsArrAdapter.setOnInstitutionListener(this)
                            binding.recyclerViewInstitutions.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = institutionsArrAdapter
                                setHasFixedSize(true)
                            }
                        } else {
                            println("No data in response")
                        }
                    } else {
                        println("Greška u odgovoru: ${response.message()}")
                        println("Status Code: ${response.code()}")
                    }
                })
        } else if (className == "Category") {
            healthcareViewModel.getCategoryQA(userPin!!)
            healthcareViewModel.getCategoryQALiveData.observe(
                viewLifecycleOwner,
                Observer { response ->
                    if (response.isSuccessful) {
                        val dataValue = response.body()
                        if (dataValue != null) {
                            val categoryList = dataValue.map { o ->
                                CategoryQAResponse(
                                    catNameSh = o.catNameSh,
                                )
                            }
                            categoryArray = categoryList.toTypedArray()
                            categoryQAAdapter =
                                CategoryQAAdapter(categoryArray!!, parentFragmentManager, className)
                            categoryQAAdapter.setOnCategoryQAListener(this)

                            binding.recyclerViewInstitutions.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = categoryQAAdapter
                                setHasFixedSize(true)
                            }
                        } else {
                            println("No data in response")
                        }
                    } else {
                        println("Greška u odgovoru: ${response.message()}")
                        println("Status Code: ${response.code()}")
                    }
                })
        } else if (className == "Topic") {
            healthcareViewModel.getTopicQA(userPin!!)
            healthcareViewModel.getTopicQALiveData.observe(
                viewLifecycleOwner,
                Observer { response ->
                    if (response.isSuccessful) {
                        val dataValue = response.body()
                        if (dataValue != null) {
                            val topicList = dataValue.map { o ->
                                TopicQAResponse(
                                    topnameSh = o.topnameSh
                                )
                            }
                            topicArray = topicList.toTypedArray()
                            topicQAAdapter = TopicQAAdapter(topicArray!!, parentFragmentManager)
                            topicQAAdapter.setOnTopicQAListener(this)

                            binding.recyclerViewInstitutions.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = topicQAAdapter
                                setHasFixedSize(true)
                            }
                        } else {
                            println("No data in response")
                        }
                    } else {
                        println("Greška u odgovoru: ${response.message()}")
                        println("Status Code: ${response.code()}")
                    }
                })
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

    private fun openFragment(nameFragment: Fragment) {
        val fragment = nameFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container_view, fragment)
            .addToBackStack(null).commit()
    }

    override fun onInstitutionSelected(institution: String?, institutionName: String) {
        healthcareViewModel.healthInstitutionName = institutionName
    }

    override fun onCategoryQASelected(categoryName: String) {
        healthcareViewModel.healthCategoryName = categoryName
    }

    override fun onTopicQASelected(topicName: String) {
        healthcareViewModel.healthTopicName = topicName
    }

    private fun filterInstitutions(query: String) {
        if (className == "Institution") {
            val filteredList = institutionalsArray?.filter { institution ->
                institution.name?.contains(query, ignoreCase = true) ?: true
            }
            if (filteredList != null) {
                updateRecyclerView(filteredList)
            }
        }

        if (className == "Category") {
            val filteredList = categoryArray?.filter { category ->
                category.catNameSh?.contains(query, ignoreCase = true) ?: true
            }
            if (filteredList != null) {
                updateCategoryRecyclerView(filteredList)
            }
        }

        if (className == "Topic") {
            val filteredList = topicArray?.filter { topic ->
                topic.topnameSh?.contains(query, ignoreCase = true) ?: true
            }
            if (filteredList != null) {
                updateTopicRecyclerView(filteredList)
            }
        }
    }

    private fun updateRecyclerView(filteredList: List<Institution>) {
        val filteredArray = filteredList.toTypedArray()
        institutionsArrAdapter.updateData(filteredArray)
    }

    private fun updateCategoryRecyclerView(filteredList: List<CategoryQAResponse>) {
        val filteredArray = filteredList.toTypedArray()
        categoryQAAdapter.updateData(filteredArray)
    }

    private fun updateTopicRecyclerView(filteredList: List<TopicQAResponse>) {
        val filteredArray = filteredList.toTypedArray()
        topicQAAdapter.updateData(filteredArray)
    }
}