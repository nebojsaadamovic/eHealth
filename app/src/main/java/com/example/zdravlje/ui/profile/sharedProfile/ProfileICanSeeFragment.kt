package com.example.zdravlje.ui.profile.sharedProfile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest.Companion.TYPE_SUBINSURED
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest.Companion.TYPE_USER
import com.example.zdravlje.data.model.response.chat.UserContact
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.ProfileSubUsersAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.ProfileUsersAdapter
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.loadImage
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentProfileICanSeeBinding

class ProfileICanSeeFragment : BaseFragment(),
    ProfileSubUsersAdapter.OnUserItemClickListener,
    ProfileUsersAdapter.OnUserItemClickListener {

    private lateinit var binding: FragmentProfileICanSeeBinding
    private lateinit var profileSubUsersAdapter: ProfileSubUsersAdapter
    private lateinit var profileUsersAdapter: ProfileUsersAdapter
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var userInfo: UserInfo
    private var dependents: List<UserContact>? = null
    private var validUsers: List<UserContact>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileICanSeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupObservers()
    }

    private fun setupUi() {
        binding.subinsuredProfile.layoutManager = LinearLayoutManager(requireContext())
        binding.usersProfile.layoutManager = LinearLayoutManager(requireContext())

        if (::userInfo.isInitialized && userInfo.sgco_id != null) {
            profileViewModel.searchPermission(
                SearchPermisionRequest(
                    type = TYPE_SUBINSURED,
                    pin = getStoredPin(),
                    sgcoId = userInfo.sgco_id.toString()
                ),
                SearchPermisionRequest(
                    type = TYPE_USER,
                    pin = getStoredPin(),
                    sgcoId = userInfo.sgco_id.toString()
                )
            )
        } else {
            Log.e("ProfileICanSeeFragment", "sgco_id is not initialized yet")
        }

    }

    private fun updateEmptyStateVisibility() {
        val noDependents = dependents.isNullOrEmpty()
        val noUsers = validUsers.isNullOrEmpty()
        binding.clWrapperData.visibility = if (noDependents && noUsers) View.VISIBLE else View.GONE
    }

    private fun setupObservers() {
        if (getStoredPin().isNotEmpty()) {
            profileViewModel.getUserInformation()
            profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo1 ->
                userInfo = userInfo1.body()!!
                if (userInfo.sgco_id != null && userInfo.sgco_id != 0) {
                    setupUi()
                } else {
                    Log.e("ProfileICanSeeFragment", "Invalid sgco_id: ${userInfo.sgco_id}")
                }
            }
        }


        profileViewModel.subinsuredLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                dependents = response.body()?.podosiguranici

                if (!dependents.isNullOrEmpty()) {
                    profileSubUsersAdapter = ProfileSubUsersAdapter(
                        dependents!!,
                        this as ProfileSubUsersAdapter.OnUserItemClickListener
                    )
                    binding.subinsuredProfile.adapter = profileSubUsersAdapter
                    binding.clSubinsured.visibility = View.VISIBLE
                    binding.subinsuredProfile.visibility = View.VISIBLE
                    binding.usersTitle.visibility = View.VISIBLE
                    profileSubUsersAdapter.updatePermitImageResource(R.drawable.ic_arrow)
                } else {
                    binding.clSubinsured.visibility = View.GONE
                    binding.subinsuredProfile.visibility = View.GONE
                    binding.usersTitle.visibility = View.GONE
                }
            }

            updateEmptyStateVisibility()
        }

        profileViewModel.usersLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val users = response.body()?.korisnici

                if (!users.isNullOrEmpty()) {
                    validUsers = users.filter { it.sharing.contact2me.permission999999 != null }
                    if (validUsers!!.isNotEmpty()) {
                        profileUsersAdapter = ProfileUsersAdapter(
                            validUsers!!,
                            this as ProfileUsersAdapter.OnUserItemClickListener
                        )
                        binding.usersProfile.adapter = profileUsersAdapter
                        binding.clUsers.visibility = View.VISIBLE
                        binding.usersProfile.visibility = View.VISIBLE
                        binding.usersTitle.visibility = View.VISIBLE
                        profileUsersAdapter.updatePermitImageResource(R.drawable.ic_arrow)
                    } else {
                        binding.clUsers.visibility = View.GONE
                        binding.usersProfile.visibility = View.GONE
                        binding.usersTitle.visibility = View.GONE
                    }
                } else {
                    binding.clUsers.visibility = View.GONE
                    binding.usersProfile.visibility = View.GONE
                    binding.usersTitle.visibility = View.GONE
                }
            }
            updateEmptyStateVisibility()
        }

        profileViewModel.searchPermissionErrorLiveData.observe(viewLifecycleOwner) { error ->
            Log.e("ProfileICanSeeFragment", "Error: ${error.message}")
        }

        profileViewModel.loginAsContactLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val responseBody = response.body()
                val token = responseBody?.token
                if (token!=null) {
                     saveToken(token.toString())
                     RetrofitInstance.AUTH_TOKEN = getStoredToken()
                }
            } else {
                Log.e("API_OBSERVER", "Fail: ${response.code()} - ${response.errorBody()?.string()}")
            }
        }

        profileViewModel.loginAsContactErrorLiveData.observe(viewLifecycleOwner) { errorMsg ->
            Log.e("API_OBSERVER", "Error: $errorMsg")
        }
    }

    override fun onItemClickFirst(item: UserContact) {
        val changeProfileBottomSheetFragment = ChangeProfileBottomSheetFragment()

        val bundle = Bundle().apply {
            putString("userId", item.sgco_id.toString())
            putString("userPin", getStoredPin())
        }
        changeProfileBottomSheetFragment.arguments = bundle

        changeProfileBottomSheetFragment.show(
            parentFragmentManager,
            changeProfileBottomSheetFragment.tag
        )
    }

    override fun onItemClickSecond(item: UserContact) {
        val addPermissionsFragment = AddPermissionsFragment()
        val activity = requireActivity() as SharedProfileActivity
        activity.startNewFragmentFromShareProfile(addPermissionsFragment)
    }

    private fun setViewModels() {
        val profileRepository = ProfileRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel =
            ViewModelProvider(requireActivity(), profileViewModelFactory)
                .get(ProfileViewModel::class.java)
    }

}
