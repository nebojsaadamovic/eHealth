package com.example.zdravlje.ui.profile.sharedProfile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.chatAddContact.ChatMyContactRadioGroupAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.createGroup.ChatSelectedContactForGroupAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChooseContactBinding

class ChooseContactFragment(

) : BaseFragment(), ChatMyContactRadioGroupAdapter.OnParticipantListener {

    lateinit var binding: FragmentChooseContactBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var chatSelectedContactForGroupAdapter: ChatSelectedContactForGroupAdapter? = null
    private lateinit var chatMyContactAdapter: ChatMyContactRadioGroupAdapter

    var contactList: List<Participant>? = null
    private var selectedItems: List<Participant>? = null
    var sgcoName: List<String>? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseContactBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupObservers()
        setupUI()
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


    private fun setupUI() {
        binding.mtvAdd.setOnClickListener {
            val addPermissionsFragment = AddPermissionsFragment()
            val bundle = Bundle().apply {
                putString("contactName", selectedItems?.firstOrNull()?.sgco_name)
                putString("contactId", selectedItems?.firstOrNull()?.sgco_id.toString())
            }

            addPermissionsFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.shared_profile_fragment_container_view, addPermissionsFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.mtvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }
            }

            override fun afterTextChanged(p0: Editable?) {
                val searchText = p0?.toString() ?: ""

                searchRunnable = Runnable {
                    profileViewModel.searchContact(
                        SearchContact(
                            pin = getStoredPin(),
                            include_friends = 2,
                            limit = 200,
                            offset = 0,
                            terms = searchText
                        )
                    )
                }

                handler.postDelayed(searchRunnable!!, 200)
            }
        })

        binding.ivBack.setOnClickListener(){
            requireActivity().onBackPressed()
        }
    }

    override fun onParticipantSelected(selectedList: List<Participant>) {
        selectedItems = selectedList
        sgcoName = selectedItems!!.map { it.sgco_name.toString() }
        binding.mtvAdd.isEnabled = true
        binding.mtvAdd.alpha = 1.0f
        binding.mtvWarningMsg.visibility = View.GONE

        updateRecyclerView(sgcoName!!)
    }

    private fun updateRecyclerView(filteredList: List<String>) {
        chatSelectedContactForGroupAdapter?.updateFilterData(filteredList)
    }

    private fun setupObservers() {
        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            val userData = userInfo.body()
            if (userData != null) {
                userId = userData.sgco_id.toString()
            }
        }

        profileViewModel.searchContact(
            SearchContact(
                pin = getStoredPin(),
                include_friends = 2,
                limit = 200,
                offset = 0,
                terms = "all"
            )
        )

        profileViewModel.searchContactResponseLiveData.observe(viewLifecycleOwner) { myContactResponse ->
            if (myContactResponse.isSuccessful) {
                contactList = myContactResponse.body()
                if (contactList?.isNotEmpty() == true) {
                    binding.clWrapperData.visibility = View.GONE

                    println("contact lista")
                    contactList!!.forEach {
                        println(it)
                    }

                    println("static lista")
                    profileViewModel.staticValidUsers.value?.forEach {
                        println(it)
                    }

                    val staticList = profileViewModel.staticValidUsers.value ?: emptyList()

                    val filteredContacts = contactList?.filter { contact ->
                        staticList.none { valid -> valid.sgco_id == contact.sgco_id }
                    }


                    println("filtered lista")
                    filteredContacts!!.forEach {
                        println(it)
                    }

                    chatMyContactAdapter = filteredContacts?.let { ChatMyContactRadioGroupAdapter(it, "ChooseContactFragment" )}!!
                    chatMyContactAdapter.setOnParticipantListener(this)
                    binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvSearch.adapter = chatMyContactAdapter
                } else {
                    binding.clWrapperData.visibility = View.VISIBLE
                }
            }
        }
    }
}