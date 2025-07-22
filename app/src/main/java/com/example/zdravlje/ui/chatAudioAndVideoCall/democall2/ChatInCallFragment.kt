package com.example.zdravlje.ui.chatAudioAndVideoCall.democall2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.democall.composesample.data.chat.messages.MessagesResponseItem
import com.app.democall.composesample.data.user.UserResponse
import com.app.democall.composesample.util.attachment.DownloadCallback
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.messages.ChatMessagesAdapter
import com.example.zdravlje.ui.chat.ChatViewModel
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChatInCallFramgentBinding

class ChatInCallFragment : Fragment() {

    private lateinit var binding: FragmentChatInCallFramgentBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var chatMessagesAdapter: ChatMessagesAdapter
    private lateinit var curentAvcUser: UserResponse
    private lateinit var chatId: String

    companion object {
        private const val ARG_CHAT_ID = "chatId"

        fun newInstance(chatId: String): ChatInCallFragment {
            return ChatInCallFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHAT_ID, chatId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatInCallFramgentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatId = arguments?.getString(ARG_CHAT_ID).toString()
        Log.i("messageList", chatId)
        setupViewModels()
        setupUI(chatId)
        subscribeToObservers()
    }

    private fun setupViewModels() {
        val activity = requireActivity() as DemoCallActivity2
        mainViewModel = activity.getMainViewModel()
        chatViewModel = activity.getChatViewModel()
        dataStoreRepository = activity.getDataStoreRepository()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(chatId: String) {
        binding.shimmer.visibility = View.VISIBLE
        mainViewModel.getSdkUser()
        mainViewModel.getMessagesForSomething()

        chatMessagesAdapter = ChatMessagesAdapter(
            mutableListOf(),
            UserResponse("", "", 0, "", "", "", "", ""),
            ::onMessageClick // Postavljanje klik listenera
        )

        binding.rvChatMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatMessages.adapter = chatMessagesAdapter

        binding.ivSendMessage.setOnClickListener {
            val messageText = binding.etChat.text.toString().trim()
            if (messageText.isNotEmpty()) {
                binding.etChat.text.clear()
                mainViewModel.sendMessage(chatId, messageText, emptyList())
                scrollToBottom()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.fragment_chat_in_call_message_cannot_be_empty),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        binding.ivSendAttachment.setOnClickListener {
            //send file from here
            // chooseFile()
            // (requireActivity() as DemoCallActivity2).showVideo()
        }

        binding.returnToCall.setOnClickListener(View.OnClickListener {
            (requireActivity() as DemoCallActivity2).showVideo()
        })
    }

    private fun onMessageClick(message: MessagesResponseItem) {
        val downloadCallback = object : DownloadCallback {
            override fun onProgressUpdate(progress: Int, size: Long) {
                Toast.makeText(requireContext(), "Uspesno ste preuzeli fajl", Toast.LENGTH_SHORT).show()
                // Implementirajte logiku za ažuriranje napretka preuzimanja
                Log.i("DownloadCallback", "Progress: $progress%, Size: $size bytes")
            }
        }
        message.attachments?.firstOrNull()?.let { mainViewModel.downloadFile(it, downloadCallback) }
    }

    private fun subscribeToObservers() {
        mainViewModel.messagesListResponseLiveData.postValue(arrayListOf())

        mainViewModel.userResponseLiveData.observe(viewLifecycleOwner) { user ->
            curentAvcUser = user
            Log.i("messageList", curentAvcUser.toString())

            mainViewModel.messagesListResponseLiveData.observe(viewLifecycleOwner) { messageList ->
                Log.i("messageList", messageList.toString())
                binding.shimmer.visibility = View.GONE
                messageList.reverse()
                chatMessagesAdapter.updateEntireMessagesList(messageList, user)
                chatMessagesAdapter.notifyDataSetChanged()
                scrollToBottom()
            }
        }
        mainViewModel.singleMessageLiveData.observe(viewLifecycleOwner) { messageResponseItem ->
            chatMessagesAdapter.addMessage(messageResponseItem)
            scrollToBottom()
        }
    }

    private fun scrollToBottom() {
        binding.rvChatMessages.scrollToPosition(chatMessagesAdapter.itemCount - 1)
    }
}