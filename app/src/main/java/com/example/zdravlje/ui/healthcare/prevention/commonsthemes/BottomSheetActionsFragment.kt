package com.example.zdravlje.ui.healthcare.prevention.commonsthemes

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.healthcare.CommonsThemesRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.response.healthcare.SocialGroupCategoryResponse
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetActionsBinding

class BottomSheetActionsFragment(
    private val data: List<SocialGroupCategoryResponse>,
    private val fragmentManager: FragmentManager
) : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetActionsBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var userPin: String? = null
    private var sgId: Int? = null
    private var sgmId: Int? = null
    var isMsgLiked: Int? = null
    var isGroupFollowed: Int? = null
    var brojLajkovaPoruke:Int? = null
    var countFollowers: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetActionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViewModels()
        subscribeToObservers()
        extractIds()
        setupUI()
    }

   private fun extractIds(){
       sgId = data.firstOrNull()?.socialGroups?.firstOrNull()?.sgId
       sgmId = data.firstOrNull()?.socialGroups?.firstOrNull()?.socialGroupMessages?.firstOrNull()?.sgmId
       brojLajkovaPoruke = data.firstOrNull()?.socialGroups?.firstOrNull()?.socialGroupMessages?.firstOrNull()?.brojLajkovaPoruke!!
       countFollowers = data.firstOrNull()?.socialGroups?.firstOrNull()?.brojPratilacaGrupe!!
       isMsgLiked = data.firstOrNull()?.socialGroups?.firstOrNull()?.socialGroupMessages?.firstOrNull()?.isMessageLiked!!
       isGroupFollowed =data.firstOrNull()?.socialGroups?.firstOrNull()?.isGroupFollowed!!
   }

    private fun setupUI() {
        binding.ivLike.setOnClickListener {
            if (isMsgLiked == 0) {
                healthcareViewModel.getLikes(CommonsThemesRequest(sgmId = sgmId, like = 1, pin = userPin))
                Toast.makeText(requireContext(),
                    getString(R.string.bottom_sheet_actions_fragment_message_marked_as_liked),
                    Toast.LENGTH_LONG).show()
            } else {
                healthcareViewModel.getLikes(CommonsThemesRequest(sgmId = sgmId, like = 0, pin = userPin))
                Toast.makeText(requireContext(),
                    getString(R.string.bottom_sheet_actions_fragment_message_removed_from_likes),
                    Toast.LENGTH_LONG).show()
            }

            refreshLikesCount()
        }

        binding.ivHeartLike.setOnClickListener {
            val message = if (isGroupFollowed == 0) {
                getString(R.string.bottom_sheet_actions_fragment_confirm_follow_group)
            } else {
                getString(R.string.bottom_sheet_actions_fragment_confirm_unfollow_group)
            }

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.bottom_sheet_actions_fragment_confirmation))
                .setMessage(message)
                .setPositiveButton(getString(R.string.bottom_sheet_actions_fragment_yes)) { _, _ ->
                    if (isGroupFollowed == 0) {
                        healthcareViewModel.followSocialGroup(CommonsThemesRequest(sgId = sgId, follow = 1, pin = userPin))
                        Toast.makeText(requireContext(),
                            getString(R.string.bottom_sheet_actions_fragment_user_followed_group),
                            Toast.LENGTH_LONG).show()
                    } else {
                        healthcareViewModel.followSocialGroup(CommonsThemesRequest(sgId = sgId, follow = 0, pin = userPin))
                        Toast.makeText(requireContext(),
                            getString(R.string.bottom_sheet_actions_fragment_user_unfollowed_group),
                            Toast.LENGTH_LONG).show()
                    }
                    refreshLikesCount()
                }
                .setNegativeButton(getString(R.string.bottom_sheet_actions_fragment_no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        binding.mbClose.setOnClickListener {
            val updatedData = data.map { category ->
                category.copy(
                    socialGroups = category.socialGroups?.map { group ->
                        group.copy(
                            brojPratilacaGrupe = countFollowers,
                            isGroupFollowed = isGroupFollowed,
                            socialGroupMessages = group.socialGroupMessages?.map { message ->
                                message.copy(
                                    brojLajkovaPoruke = brojLajkovaPoruke,
                                    isMessageLiked = isMsgLiked
                                )
                            }
                        )
                    }
                )
            }
            val fragment = PreventionInfoDetailsFragment(updatedData)
            fragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, fragment)
                .addToBackStack(null).commit()
            dismiss()
        }
    }

    private fun refreshLikesCount() {
        healthcareViewModel.getCommonsThemes(CommonsThemesRequest(sgmId = sgmId))
        healthcareViewModel.getCommonsThemesLiveData.observe(viewLifecycleOwner, Observer { responseOb ->
                if (responseOb.isSuccessful) {
                    val dataValue = responseOb.body()
                    if (dataValue != null) {
                        val likeData1 = dataValue.firstOrNull()?.socialGroups?.firstOrNull()?.socialGroupMessages
                        if (likeData1 != null && likeData1.isNotEmpty()) {
                            brojLajkovaPoruke = likeData1.firstOrNull()?.brojLajkovaPoruke ?: 0
                            isMsgLiked = likeData1.firstOrNull()?.isMessageLiked!!
                            binding.mtvLike.text = brojLajkovaPoruke.toString()+" ${getString(R.string.bottom_sheet_actions_fragment_likes)}"
                        } else {
                            binding.mtvLike.text = getString(R.string.bottom_sheet_actions_fragment_like)
                        }

                        val followData = dataValue.firstOrNull()?.socialGroups
                        if (followData != null && followData.isNotEmpty()) {
                            countFollowers = followData.firstOrNull()?.brojPratilacaGrupe ?: 0
                            isGroupFollowed = followData.firstOrNull()?.isGroupFollowed!!
                            binding.mtvHeartLike.text = countFollowers.toString()+" ${getString(R.string.bottom_sheet_actions_fragment_followers)}"
                        } else {
                            binding.mtvHeartLike.text = getString(R.string.bottom_sheet_actions_fragment_follow_group)
                        }
                    }
                } else {
                    println("Greška u odgovoru: ${responseOb.message()}")
                }
            })
    }

    private fun subscribeToObservers() {
        binding.mtvLike.text = getString(R.string.bottom_sheet_actions_fragment_like)
        userPin=getStoredPin()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
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
}