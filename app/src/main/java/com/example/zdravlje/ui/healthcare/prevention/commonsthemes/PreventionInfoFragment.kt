package com.example.zdravlje.ui.healthcare.prevention.commonsthemes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.CommonsThemesRequest
import com.example.zdravlje.data.model.response.healthcare.SocialGroup
import com.example.zdravlje.data.model.response.healthcare.SocialGroupCategoryResponse
import com.example.zdravlje.data.model.response.healthcare.SocialGroupMessage
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.SocialGroupsInfoAdapter
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.databinding.FragmentPreventionInfoBinding

class PreventionInfoFragment(
    private val sgmId: Int?
) : Fragment() {

    private lateinit var binding: FragmentPreventionInfoBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var socialGroupsInfoAdapter: SocialGroupsInfoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreventionInfoBinding.inflate(layoutInflater)
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
        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    private fun setAdapter() {
        healthcareViewModel.getCommonsThemes(CommonsThemesRequest(sgmId = sgmId))
        healthcareViewModel.getCommonsThemesLiveData.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                showHideShimmer(false)
                val dataValue = response.body()
                if (dataValue != null) {
                    val listData = dataValue.map { responseObject ->
                        SocialGroupCategoryResponse(
                            sgcatNameSh = responseObject.sgcatNameSh,
                            socialGroups = responseObject.socialGroups?.map { group ->
                                SocialGroup(
                                    sgId = group.sgId,
                                    sgTimeCreated = group.sgTimeCreated,
                                    brojPratilacaGrupe = group.brojPratilacaGrupe,
                                    isGroupFollowed = group.isGroupFollowed,
                                    socialGroupMessages = group.socialGroupMessages?.map { message ->
                                        SocialGroupMessage(
                                            sgmId=message.sgmId,
                                            brojLajkovaPoruke= message.brojLajkovaPoruke,
                                            sgmMessage = message.sgmMessage,
                                            isMessageLiked = message.isMessageLiked
                                        )
                                    } ?: emptyList()
                                )
                            } ?: emptyList()
                        )
                    }

                    socialGroupsInfoAdapter = SocialGroupsInfoAdapter(listData, parentFragmentManager)
                    val linearLayoutManager = LinearLayoutManager(requireContext())
                    binding.reciclerViewCommonThemesInfo.layoutManager = linearLayoutManager
                    binding.reciclerViewCommonThemesInfo.adapter = socialGroupsInfoAdapter
                }
            } else {
                println("Greška u odgovoru: ${response.message()}")
            }
        })
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

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }
}