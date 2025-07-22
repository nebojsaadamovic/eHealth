package com.example.zdravlje.ui.chat.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.app.democall.composesample.data.chat.ChatParticipant
import com.example.zdravlje.data.model.request.chat.AcceptContactRequest
import com.example.zdravlje.data.model.request.chat.RemoveContactRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.singleAndGroupMessages.ChatMessagesActivity
import com.example.zdravlje.utilities.loadImage
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChatAcceptContactBinding
import kotlinx.coroutines.launch

class ChatAcceptContactFragment(
    private var chatParticipant: ChatParticipant? = null,
    private var avatarResource: String? = null,
    private val chatId: String? = null
) : BaseFragment() {

    lateinit var binding: FragmentChatAcceptContactBinding
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var chatViewModel: ChatViewModel
    private var userPin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentChatAcceptContactBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        observePin()
        setupUI()
        subscribeToObservers()
    }

    private fun setupUI() {
        val dataItem = chatParticipant

        if (dataItem != null) {
            binding.sgcoName.text = dataItem.sgcoName
            binding.tvEmail.text = getString(R.string.chat_inbox_adapter_new_contact)

            binding.ivAvatar.loadImage(
                dataItem.avatar,
                R.drawable.ic_contact_avatar
            )
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.mtvCancelRequest.setOnClickListener {
            binding.progressBar.visibility=View.VISIBLE
            userPin?.let { pin ->
                chatParticipant?.sgcoId?.let { sgcoId ->
                    val request = RemoveContactRequest(pin = pin, sgco_id = sgcoId, vcc_id = chatId)
                    chatViewModel.denyContactRequest(request)
                }
            }
        }

        binding.mtvAcceptRequest.setOnClickListener() {
            binding.progressBar.visibility=View.VISIBLE
            userPin?.let { pin ->
                chatParticipant?.sgcoId?.let { sgcoId ->
                    val request = AcceptContactRequest(pin = pin, sgco_id = sgcoId, vcc_id = chatId)
                    chatViewModel.acceptContactRequest(request)
                }
            }
        }
    }

    private fun observePin() {
        userPin=getStoredPin()
    }

    private fun subscribeToObservers() {
        chatViewModel.denyContactRequestLiveData.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.isSuccessful) {
                binding.progressBar.visibility=View.GONE
                requireActivity().finish()
            }
        }

        chatViewModel.acceptContactRequestLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
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

