package com.example.zdravlje.ui.chat.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.app.democall.composesample.data.chat.ChatParticipant
import com.example.zdravlje.data.model.request.chat.RemoveContactRequest
import com.example.zdravlje.data.model.response.chat.SearchContactResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.singleAndGroupMessages.ChatMessagesActivity
import com.example.zdravlje.utilities.loadImage
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChatAddContactDetailsBinding
import kotlinx.coroutines.launch


class ChatSendContactDetailsFragment(
    private var searchContactItem: SearchContactResponse? = null,
    private var chatParticipant: ChatParticipant? = null,
    private var avatarResource: Any ?= null,
    private val chatId: String? = null,
) : BaseFragment() {

    lateinit var binding: FragmentChatAddContactDetailsBinding
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var chatViewModel: ChatViewModel
    private var userPin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatAddContactDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        observePin()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {
        val dataItem=chatParticipant
        if (dataItem != null) {
            binding.sgcoName.text = dataItem.sgcoName ?: ""
            binding.tvEmail.text = getString(R.string.chat_inbox_adapter_send_request)
            try {
                // Ako je avatarResource broj, učitaj ga kao resurs
                binding.ivAvatar.setImageResource(avatarResource.toString().toInt())
            } catch (e: NumberFormatException) {
                // Ako nije broj, onda ga tretiraj kao base64 ili URL
                binding.ivAvatar.loadImage(
                    avatarResource.toString(),
                    R.drawable.ic_contact_avatar
                )
            }
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().finish()
        }

        binding.mtvCancelRequest.setOnClickListener() {
            binding.progressBar.visibility=View.VISIBLE
            userPin?.let { pin ->
                chatParticipant?.sgcoId?.let { sgcoId ->
                    val request = RemoveContactRequest(pin = pin, sgco_id = sgcoId, vcc_id = chatId)
                    chatViewModel.cancelFriendRequest(request)
                }
            }
        }
    }

    private fun observePin() {
        userPin=getStoredPin()
    }
    private fun subscribeToObservers() {
        chatViewModel.cancelFriendRequestLiveData.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                requireActivity().finish()
            }
        }
    }


    private fun setupViewModels() {
        val activity = requireActivity() as ChatMessagesActivity
        chatViewModel = activity.getChatViewModel()
        dataStoreRepository = activity.getDataStoreRepository()
    }
}