package com.example.zdravlje.ui.chat.createGroup

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.democall.composesample.util.observers.network.ConnectivityObserverImpl
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.repository.ChatRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.avc.SampleApplication
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.services.di.MainViewModelFactory
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.createGroup.ChatMyContactAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.createGroup.ChatSelectedContactForGroupAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.ChatViewModelFactory
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentCreateGroupMyContactBinding

class CreateGroupMyContactFragment(var chatTitle: String) : BaseFragment(),
    ChatMyContactAdapter.OnParticipantListener {

    private lateinit var binding: FragmentCreateGroupMyContactBinding
    private lateinit var chatViewModel: ChatViewModel

    private lateinit var chatMyContactAdapter: ChatMyContactAdapter
    private var chatSelectedContactForGroupAdapter: ChatSelectedContactForGroupAdapter? = null

    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    var contactList: List<Participant>? = null
    private var selectedItems: List<Participant>? = null
    private var userPin: String? = null
    var sgcoName: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateGroupMyContactBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        subscribeToObservers()
        observePin()
    }

    private fun observePin() {
        userPin=getStoredPin()
        setupUI()
    }

    private fun setupViewModels() {
        val chatRepository = ChatRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val chatViewModelFactory = ChatViewModelFactory(chatRepository, dataStoreRepository)
        chatViewModel = ViewModelProvider(requireActivity(), chatViewModelFactory)
            .get(ChatViewModel::class.java)

        val sampleApp = requireActivity().application as SampleApplication
        val avcSdkManager = sampleApp.getAVC()
        val mainViewModelFactory = MainViewModelFactory(
            avcSdkManager,
            ConnectivityObserverImpl(requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        )

        mainViewModel = ViewModelProvider(
            requireActivity(),
            mainViewModelFactory
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {
        binding.mtvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                filterName(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.mtvAdd.setOnClickListener(View.OnClickListener {
            val userIds: List<Int> =
                selectedItems?.mapNotNull { it.sgco_id} ?: emptyList()
            mainViewModel.createChat(chatTitle, userIds, userIds.size + 1, allowAnonymous = false)
        })

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        setupMyContactsRecyclerView()
    }

    private fun setupMyContactsRecyclerView() {
        chatViewModel.searchContact(SearchContact(terms = "all", pin = userPin))
    }

    private fun subscribeToObservers() {
        chatViewModel.searchContactResponseLiveData.observe(viewLifecycleOwner) { myContactResponse ->
            if (myContactResponse.isSuccessful) {
                contactList = myContactResponse.body()
                chatMyContactAdapter = contactList?.let { ChatMyContactAdapter(it) }!!
                chatMyContactAdapter.setOnParticipantListener(this)
                binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
                binding.rvSearch.adapter = chatMyContactAdapter
            }
        }

        mainViewModel.createChatResponseLiveData.observe(viewLifecycleOwner) { createChatResponseLiveData ->
            requireActivity().finish()
        }
    }

    override fun onParticipantSelected(selectedList: List<Participant>) {
        selectedItems = selectedList
        chatSelectedContactForGroupAdapter = ChatSelectedContactForGroupAdapter(sgcoName)
        binding.rvSearchSelected.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSearchSelected.adapter = chatSelectedContactForGroupAdapter
        sgcoName = selectedItems!!.map { it.sgco_name.toString() }
        println(selectedItems!!.map { it.sgco_name })
        if (sgcoName!!.size < 2) {
            binding.mtvAdd.isEnabled = false
            binding.mtvAdd.alpha = 0.2f
            binding.mtvWarningMsg.visibility = View.VISIBLE
            binding.mtvWarningMsg.text = getString(R.string.fragment_create_group_my_contact_wraning_msg)
        }else{
            binding.mtvAdd.isEnabled = true
            binding.mtvAdd.alpha = 1.0f
            binding.mtvWarningMsg.visibility = View.GONE
        }
        updateRecyclerView(sgcoName!!)
    }

    private fun updateRecyclerView(filteredList: List<String>) {
        chatSelectedContactForGroupAdapter?.updateFilterData(filteredList)
    }

    private fun updateRecyclerView2(filteredList: List<Participant>) {
        chatMyContactAdapter.updateFilterData2(filteredList)
    }

    private fun filterName(query: String) {
        val filteredList = contactList?.filter { data ->
            data.sgco_name?.contains(query, ignoreCase = true) == true
        }

        if (!filteredList.isNullOrEmpty()) {
            binding.clWrapperData.visibility = View.GONE
            updateRecyclerView2(filteredList)
        } else {
            binding.clWrapperData.visibility = View.VISIBLE
        }
    }
}
