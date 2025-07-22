package com.example.zdravlje.ui.chat.options

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.democall.composesample.util.observers.network.ConnectivityObserverImpl
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.request.chat.SendContactRequest
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.repository.ChatRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.avc.SampleApplication
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.services.di.MainViewModelFactory
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.chatAddContact.ChatMyContactRadioGroupAdapter
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.createGroup.ChatSelectedContactForGroupAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.ChatViewModelFactory
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChatAddContactBinding

class ChatAddContactFragment() : BaseFragment(),
    ChatMyContactRadioGroupAdapter.OnParticipantListener {

    private lateinit var binding: FragmentChatAddContactBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatMyContactAdapter: ChatMyContactRadioGroupAdapter
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
        binding = FragmentChatAddContactBinding.inflate(inflater)
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
                val searchText = s.toString()

                if (searchText.length < 3) {
                    if (::chatMyContactAdapter.isInitialized) {
                        chatMyContactAdapter.clearList()
                        binding.clWrapperData.visibility = View.VISIBLE
                    }
                    return
                }

                chatViewModel.searchContact(
                    SearchContact(
                        terms = searchText,
                        pin = userPin,
                        include_friends = 0,
                        limit = 10,
                        offset = 0
                    )
                )
                //filterName(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.mtvAdd.setOnClickListener {
            val id = selectedItems?.firstOrNull()?.sgco_id

            if (id != null) {
                chatViewModel.sendContactRequest(
                    SendContactRequest(
                        pin = userPin,
                        sgco_id = id
                    )
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "Niste izabrali validan kontakt.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        /*
               setupMyContactsRecyclerView()
        */
    }

    /*    private fun setupMyContactsRecyclerView() {
            chatViewModel.searchContact(SearchContact(terms = "all", pin = userPin, include_friends = 0, limit = 10, offset = 0))
        }
    */
    @SuppressLint("SuspiciousIndentation")
    private fun subscribeToObservers() {
        chatViewModel.sendContactResponseLiveData.observe(viewLifecycleOwner) { sendContactResponse ->
            requireActivity().finish()
        }

        chatViewModel.sendContactResponseErrorLiveData.observe(viewLifecycleOwner) { sendContactErrorResponse ->
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_chat_add_contact_user_add_error),
                Toast.LENGTH_SHORT
            ).show()
        }

        chatViewModel.searchContactResponseLiveData.observe(viewLifecycleOwner) { myContactResponse ->
            if (myContactResponse.isSuccessful) {
                contactList = myContactResponse.body()
                if (contactList?.isNotEmpty() == true) {
                    binding.clWrapperData.visibility = View.GONE
                    chatMyContactAdapter = contactList?.let { ChatMyContactRadioGroupAdapter(it) }!!
                    chatMyContactAdapter.setOnParticipantListener(this)
                    binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvSearch.adapter = chatMyContactAdapter
                }else {
                    binding.clWrapperData.visibility = View.VISIBLE
                }
            }
        }

        mainViewModel.createChatResponseLiveData.observe(viewLifecycleOwner) { createChatResponseLiveData ->
            requireActivity().finish()
        }
    }

    private fun updateRecyclerView(filteredList: List<String>) {
        chatSelectedContactForGroupAdapter?.updateFilterData(filteredList)
    }

    override fun onParticipantSelected(selectedList: List<Participant>) {
        selectedItems = selectedList
        chatSelectedContactForGroupAdapter = ChatSelectedContactForGroupAdapter(sgcoName)
        binding.rvSearchSelected.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSearchSelected.adapter = chatSelectedContactForGroupAdapter
        sgcoName = selectedItems!!.map { it.sgco_name.toString() }

        binding.mtvAdd.isEnabled = true
        binding.mtvAdd.alpha = 1.0f
        binding.mtvWarningMsg.visibility = View.GONE

        updateRecyclerView(sgcoName!!)
    }

//    private fun updateRecyclerView2(filteredList: List<SearchContactResponse>) {
//        chatMyContactAdapter.updateFilterData2(filteredList)
//    }

/*    private fun filterName(query: String) {
        val filteredList = contactList?.filter { data ->
            data.sgco_name?.contains(query, ignoreCase = true) == true
        }
        if (filteredList != null) {
            updateRecyclerView2(filteredList)
        }
    }
*/
}