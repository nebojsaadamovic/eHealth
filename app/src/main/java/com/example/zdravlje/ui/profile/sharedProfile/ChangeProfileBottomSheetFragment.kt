package com.example.zdravlje.ui.profile.sharedProfile

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.profile.SharedProfile
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChangeProfileBottomSheetBinding

class ChangeProfileBottomSheetFragment : BaseBottomSheetDialogFragment() {

    lateinit var binding: FragmentChangeProfileBottomSheetBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    private var userPin: String? = null
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeProfileBottomSheetBinding.inflate(layoutInflater)

        arguments?.let { bundle ->
            userId = bundle.getString("userId")
            userPin = bundle.getString("userPin")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
        setupObservers()
    }

    private fun setViewModels() {
        val profileRepository = ProfileRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val profileViewModelFactory = ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(requireActivity(), profileViewModelFactory).get(ProfileViewModel::class.java)
    }


    private fun setupUI() {
        binding.mbCancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        binding.mbContinue.setOnClickListener(View.OnClickListener {
            profileViewModel.getUserInformation()
            profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
                if (userInfo.isSuccessful) {
                    val userData = userInfo.body()
                    val sharedPrefs = context?.getSharedPreferences("shared_user", Context.MODE_PRIVATE)
                    sharedPrefs?.edit()?.apply {
                        putString("shared_user_id", userData?.sgco_id.toString())
                        putString("shared_user_pin", userData?.pat_pin)
                        putString("shared_user_first_name", userData?.pat_first_name)
                        putString("shared_user_last_name", userData?.pat_last_name)
                        putString("shared_user_user_image", userData?.avt_encoded_value ?: "")
                        putString("shared_user_gender", (userData?.pat_sex ?: "").toString())
                        putString("shared_token", getToken())
                        apply()
                    }
                }

                requireActivity().finish()
               // CommonUtils.openFragmentHome(requireActivity(), HomeFragment())
            }

            println("sgcioooiidddddddddddd")
            profileViewModel.loginAsContact(
                SharedProfile(
                    sgcoId = userId,
                    pin = userPin
                )
            )
        })
    }
    private fun setupObservers() {
        profileViewModel.closeBottomSheetEvent.observe(viewLifecycleOwner) { shouldClose ->
            if (shouldClose) {
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

}