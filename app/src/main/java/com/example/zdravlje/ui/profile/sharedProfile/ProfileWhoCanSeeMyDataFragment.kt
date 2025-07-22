package com.example.zdravlje.ui.profile.sharedProfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest.Companion.TYPE_DOCTOR
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest.Companion.TYPE_PHARMACIST
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest.Companion.TYPE_SUBINSURED
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest.Companion.TYPE_USER
import com.example.zdravlje.data.model.response.chat.UserContact
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.ProfileDoctorsAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.ProfilePharmacistAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.ProfileSubUsersAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.ProfileUsersAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentProfileWhoCanSeeMyDataBinding

class ProfileWhoCanSeeMyDataFragment() : BaseFragment(),
    ProfileSubUsersAdapter.OnUserItemClickListener,
    ProfileUsersAdapter.OnUserItemClickListener {

    lateinit var binding: FragmentProfileWhoCanSeeMyDataBinding
    private lateinit var profileUsersAdapter: ProfileUsersAdapter
    private lateinit var profileDoctorsAdapter: ProfileDoctorsAdapter
    private lateinit var profilePharmacistAdapter: ProfilePharmacistAdapter
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var userInfo: UserInfo
    private var validDoctors: List<UserContact>? = null
    private var validUsers: List<UserContact>? = null
    private var validPharmacist: List<UserContact>? = null
    private var usersLoaded = false
    private var doctorsLoaded = false
    private var pharmacistsLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileWhoCanSeeMyDataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupObservers()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setupUi() {
        binding.usersProfile.layoutManager = LinearLayoutManager(requireContext())
        binding.doctorsProfile.layoutManager = LinearLayoutManager(requireContext())

            profileViewModel.searchPermission(
                SearchPermisionRequest(type = TYPE_DOCTOR, pin = getStoredPin(), sgcoId = userInfo.sgco_id.toString()),
                SearchPermisionRequest(type = TYPE_USER, pin = getStoredPin(), sgcoId = userInfo.sgco_id.toString()),
                SearchPermisionRequest(type = TYPE_PHARMACIST, pin = getStoredPin(),sgcoId = userInfo.sgco_id.toString()),
                SearchPermisionRequest(type = TYPE_SUBINSURED, pin = getStoredPin(),sgcoId = userInfo.sgco_id.toString())
            )
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

    private fun updateEmptyStateVisibilityWhenAllLoaded() {
        if (usersLoaded && doctorsLoaded && pharmacistsLoaded) {
            val noPharmacist = validPharmacist.isNullOrEmpty()
            val noDoctors = validDoctors.isNullOrEmpty()
            val noUsers = validUsers.isNullOrEmpty()
            binding.clWrapperData.visibility = if (noPharmacist && noUsers && noDoctors) View.VISIBLE else View.GONE
        }
    }

    private fun setupObservers() {
        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo1 ->
            userInfo = userInfo1.body()!!
            if (userInfo.sgco_id != null && userInfo.sgco_id != 0) {
                setupUi()
            } else {
                Log.e("ProfileICanSeeFragment", "Invalid sgco_id: ${userInfo.sgco_id}")
            }
        }

        profileViewModel.usersLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val users = response.body()?.korisnici

                if (!users.isNullOrEmpty()) {
                    validUsers =
                        users.filter { it.sharing.me2contact.permission999999 != null
                                || it.sharing.me2contact.permission1 != null
                                || it.sharing.me2contact.permission2 != null
                                || it.sharing.me2contact.permission3 != null
                                || it.sharing.me2contact.permission4 != null
                                || it.sharing.me2contact.permission5 != null
                    }
                    if (validUsers!!.isNotEmpty()) {
                        profileViewModel.staticValidUsers.value = validUsers

                        profileUsersAdapter = ProfileUsersAdapter(validUsers!!, this)
                        binding.usersProfile.adapter = profileUsersAdapter
                        binding.clUsers.visibility=View.VISIBLE
                        binding.usersProfile.visibility = View.VISIBLE
                        binding.usersTitle.visibility = View.VISIBLE
                        profileUsersAdapter.updatePermitImageResource(R.drawable.ic_permits_folder)

                    } else {
                        binding.clUsers.visibility=View.GONE
                        binding.usersProfile.visibility = View.GONE
                        binding.usersTitle.visibility = View.GONE
                    }

                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("ProfileICanSeeFragment", "Error fetching users: $errorMessage")
                }
            }

            usersLoaded = true
            updateEmptyStateVisibilityWhenAllLoaded()
        }

        profileViewModel.doctorsLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val doctors = response.body()?.doktori
                if (!doctors.isNullOrEmpty()) {
                     validDoctors =
                        doctors.filter { it.sharing.me2contact.permission999999 != null
                                || it.sharing.me2contact.permission1 != null
                                || it.sharing.me2contact.permission2 != null
                                || it.sharing.me2contact.permission3 != null
                                || it.sharing.me2contact.permission4 != null
                                || it.sharing.me2contact.permission5 != null
                        }
                    if (validDoctors!!.isNotEmpty()) {
                        profileDoctorsAdapter =ProfileDoctorsAdapter(validDoctors!!, this)
                        binding.doctorsProfile.adapter = profileDoctorsAdapter
                        binding.clDoctors.visibility = View.VISIBLE
                        binding.doctorsProfile.visibility = View.VISIBLE
                        binding.doctorsTitle.visibility = View.VISIBLE
                        profileDoctorsAdapter.updatePermitImageResource(R.drawable.ic_permits_folder)
                    }else{
                        binding.clDoctors.visibility=View.GONE
                        binding.doctorsProfile.visibility = View.GONE
                        binding.doctorsTitle.visibility = View.GONE
                    }
                } else {
                    binding.clDoctors.visibility=View.GONE
                    binding.doctorsProfile.visibility = View.GONE
                    binding.doctorsTitle.visibility = View.GONE
                }
            }
            doctorsLoaded = true
            updateEmptyStateVisibilityWhenAllLoaded()
        }

        profileViewModel.pharmacistLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
              val pharmacist = response.body()?.farmaceuti
                if (!pharmacist.isNullOrEmpty()) {
                    validPharmacist =
                        pharmacist.filter { it.sharing.me2contact.permission999999 != null
                                || it.sharing.me2contact.permission1 != null
                                || it.sharing.me2contact.permission2 != null
                                || it.sharing.me2contact.permission3 != null
                                || it.sharing.me2contact.permission4 != null
                                || it.sharing.me2contact.permission5 != null
                        }

                    profilePharmacistAdapter =
                        ProfilePharmacistAdapter(validPharmacist!!, this)
                    binding.pharmacistProfile.adapter = profilePharmacistAdapter
                    binding.clPharmacists.visibility=View.VISIBLE
                    binding.pharmacistProfile.visibility = View.VISIBLE
                    binding.pharmacistTitle.visibility = View.VISIBLE
                    profilePharmacistAdapter.updatePermitImageResource(R.drawable.ic_permits_folder)
                } else {
                    binding.clPharmacists.visibility=View.GONE
                    binding.pharmacistProfile.visibility = View.GONE
                    binding.pharmacistTitle.visibility = View.GONE
                }
            }
            pharmacistsLoaded = true
            updateEmptyStateVisibilityWhenAllLoaded()
        }

        profileViewModel.searchPermissionErrorLiveData.observe(viewLifecycleOwner) { error ->
            Log.e("ProfileICanSeeFragment", "Error: ${error.message}")
        }
    }

    override fun onItemClickFirst(item: UserContact) {
        val changeProfileBottomSheetFragment = ChangeProfileBottomSheetFragment()
        changeProfileBottomSheetFragment.show(
            parentFragmentManager,
            changeProfileBottomSheetFragment.tag
        )
    }

    override fun onItemClickSecond(user: UserContact) {
        val addPermissionsFragment = AddPermissionsFragment().apply {
            arguments = Bundle().apply {
                putString("class_name", "ProfileWhoCanSeeMyDataFragment") // Koristite jedinstveni identifikator
                putSerializable("userId", user)
            }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.shared_profile_fragment_container_view, addPermissionsFragment)
            .addToBackStack(null)
            .commit()
    }
}

