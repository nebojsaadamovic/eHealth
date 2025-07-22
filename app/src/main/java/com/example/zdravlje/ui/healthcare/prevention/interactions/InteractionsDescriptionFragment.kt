package com.example.zdravlje.ui.healthcare.prevention.interactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.healthcare.FeedbackRequest
import com.example.zdravlje.data.model.request.healthcare.QueryItem
import com.example.zdravlje.data.model.request.healthcare.StaticRagRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentInteractionsDescriptionBinding
import retrofit2.Response

class InteractionsDescriptionFragment : BaseFragment() {

    private lateinit var binding: FragmentInteractionsDescriptionBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var responseId: String
    private lateinit var levelInteraction: String
    private lateinit var content: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInteractionsDescriptionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()

        parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { key, bundle ->
            val className = bundle.getString("className")
            if (className == "BottomSheetLikesInteractionsFragment") {
                binding.mtvEditInfo.visibility = View.VISIBLE
                binding.mtvEditInfo.text = getString(R.string.fragment_interactions_description_level_interaction)
                binding.mbEdit.visibility = View.VISIBLE
                binding.etContent.isEnabled = true
                binding.etContent.requestFocus()
            }
        }

        parentFragmentManager.setFragmentResultListener("rk-BottomSheetInteractionLevelFragment", viewLifecycleOwner) { key, bundle ->
            val className = bundle.getString("className")
            val selectedText = bundle.getString("selectedText")
            if (className == "BottomSheetInteractionLevelFragment") {
                binding.mtvInfo.text = selectedText
                binding.mtvSave.visibility = View.VISIBLE
                binding.ivCompareMedicamente.visibility = View.GONE

                if (selectedText != null) {
                    levelInteraction = selectedText
                }
                when (selectedText) {
                    getString(R.string.fragment_interactions_description_no_interaction) -> {
                        binding.mcvColor.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.green_light_C6E0B4
                            )
                        )
                    }

                    getString(R.string.fragment_interactions_description_moderate_interaction)-> {
                        binding.mcvColor.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.orange_light_FDCEAB
                            )
                        )
                    }

                    getString(R.string.fragment_interactions_description_severe_interaction) -> {
                        binding.mcvColor.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.pink_light_F8BABA
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.mtvSave.setOnClickListener() {
            val content = binding.etContent.text.toString()
            var score: String? = null
            when (levelInteraction) {
                getString(R.string.fragment_interactions_description_no_interaction) -> score = "1"
                getString(R.string.fragment_interactions_description_moderate_interaction) -> score = "3"
                getString(R.string.fragment_interactions_description_severe_interaction)  -> score = "5"
            }

            healthcareViewModel.saveFeedback(20, FeedbackRequest(response_id = responseId, answer = content, score = score))
        }

        binding.ivCompareMedicamente.setOnClickListener(){
            val bottomSheet = BottomSheetLikesInteractionsFragment(responseId, content)
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }

        binding.mbEdit.setOnClickListener(){
            val bottomSheet = BottomSheetInteractionLevelFragment(levelInteraction)
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }

        binding.ivBack.setOnClickListener(){
            requireActivity().onBackPressed()
        }
    }

    private fun subscribeToObservers() {

        healthcareViewModel.getSaveFeedbackLiveData.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                if (response.isSuccessful) {
                    val message = response.body()?.get("message")?.asString
                    Toast.makeText(requireActivity(), message ?: "Uspešno", Toast.LENGTH_SHORT).show()
                    CommonUtils.openFragmentHome(requireActivity(), InteractionFragment())
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }

            healthcareViewModel.getSaveFeedbackLiveData.postValue(null)
        }


        healthcareViewModel.getStaticRagLiveData.value = Response.success(null)
        val innList = healthcareViewModel.listNameInn.value ?: emptyList()

        val queryItems = innList.map { item ->
            QueryItem(
                name = item.name,
                type = "DRUG"
            )
        }

        healthcareViewModel.getStaticRag(
            patientId = 20,
            StaticRagRequest(
                query = queryItems
            )
        )

        healthcareViewModel.getStaticRagLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val data = response.body()
                content = data?.answer ?: ""
                binding.etContent.setText(data?.answer ?: "")

                if (data?.score != null) {

                    responseId = data.id

                    when (data?.score) {
                        1 -> {
                            levelInteraction = getString(R.string.fragment_interactions_description_no_interaction)
                            binding.mtvInfo.text = levelInteraction
                            binding.mcvColor.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.green_light_C6E0B4
                                )
                            )
                            binding.mcvInfo.visibility = View.VISIBLE
                        }

                        3 -> {
                            levelInteraction = getString(R.string.fragment_interactions_description_moderate_interaction)
                            binding.mtvInfo.text = levelInteraction
                            binding.mcvColor.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.orange_light_FDCEAB
                                )
                            )
                            binding.mcvInfo.visibility = View.VISIBLE
                        }

                        5 -> {
                            levelInteraction = getString(R.string.fragment_interactions_description_severe_interaction)
                            binding.mtvInfo.text = levelInteraction
                            binding.mcvColor.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.pink_light_F8BABA
                                )
                            )
                            binding.mcvInfo.visibility = View.VISIBLE
                        }
                    }
                }
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
}