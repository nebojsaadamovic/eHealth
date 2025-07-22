package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.info.ParticipantAdapter
import com.nites.ezdravljeapp.databinding.FragmentGroupParticipantsBinding

class GroupParticipantsFragment(private val chatId: String) : Fragment() {

    lateinit var binding: FragmentGroupParticipantsBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var participantAdapter: ParticipantAdapter
    private var participants: ArrayList<String>? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupParticipantsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
    }

    private fun setViewModels() {
        val activity = requireActivity() as ChatMessagesActivity
        mainViewModel = activity.getMainViewModel()
        dataStoreRepository = DataStoreRepository(requireActivity())
    }

    private fun setupUI() {
        if (chatId != null) {
            mainViewModel.startChat(chatId)
        }

        mainViewModel.startChatResponseLiveData.observe(viewLifecycleOwner, Observer { response ->
            participants = response.participants.map { it.sgcoName }
                .toCollection(ArrayList())
            participantAdapter = ParticipantAdapter(participants!!)
            binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
            binding.rvSearch.adapter = participantAdapter
        })

        binding.mtvSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val searchText = p0?.toString()?.trim() ?: ""

                if (searchText.length < 3) {
                    participants?.let { participantAdapter.updateData(it) }
                    return
                }

                searchRunnable = Runnable {
                    val filteredParticipants = filterParticipants(searchText)

                    if (filteredParticipants.isEmpty()){
                        binding.clWrapperData.visibility=View.VISIBLE
                    }
                    else{
                        binding.clWrapperData.visibility=View.GONE
                        participantAdapter.updateData(filteredParticipants)
                    }
                }
                handler.postDelayed(searchRunnable!!, 500)
            }
        })

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun filterParticipants(query: String): List<String> {
        val trimmedQuery = query.trim().toLowerCase().replace("\\s+".toRegex(), "")
        val filteredList = participantAdapter.getAllParticipants().filter {
            val participantName = it.trim().toLowerCase().replace("\\s+".toRegex(), "")
            participantName.contains(trimmedQuery)
        }

        return filteredList
    }
}