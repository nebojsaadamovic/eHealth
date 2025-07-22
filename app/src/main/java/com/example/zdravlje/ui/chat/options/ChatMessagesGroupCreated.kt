package com.example.zdravlje.ui.chat.options

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.democall.composesample.util.observers.network.ConnectivityObserverImpl
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.avc.SampleApplication
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.services.di.MainViewModelFactory
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChatMessagesGroupCreatedBinding

class ChatMessagesGroupCreated : Fragment() {

    private lateinit var binding: FragmentChatMessagesGroupCreatedBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatMessagesGroupCreatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModels()
    }

    private fun setupViewModels() {
        val sampleApp = requireActivity().application as SampleApplication
        // Dohvatanje avcSdkManager preko SampleApplication
        val avcSdkManager = sampleApp.getAVC()

        val mainViewModelFactory = MainViewModelFactory(
            avcSdkManager,
            ConnectivityObserverImpl(requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        )

        mainViewModel = ViewModelProvider(
            requireActivity(),
            mainViewModelFactory
        ).get(MainViewModel::class.java)

        mainViewModel.createChatResponseLiveData.observe(viewLifecycleOwner) { createChatResponseLiveData ->
            binding.mvChatName.text = createChatResponseLiveData.title
            binding.mvChatDesc.text =
                createChatResponseLiveData.participants.size.toString() + getString(R.string.fragment_chat_messages_group_created_participant)
            if (createChatResponseLiveData.participants.size > 2) {
                binding.ivParticipantPhoto.setImageResource(R.drawable.ic_group_chat_avatar)
            }
        }
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener() {
            requireActivity().finish()
        }
    }
}