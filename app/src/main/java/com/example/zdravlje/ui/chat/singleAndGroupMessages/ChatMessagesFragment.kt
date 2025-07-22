package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.democall.composesample.data.chat.AttachmentRequest
import com.app.democall.composesample.data.chat.ChatParticipant
import com.app.democall.composesample.data.chat.list.ChatListResponseItem
import com.app.democall.composesample.data.chat.messages.MessagesResponseItem
import com.app.democall.composesample.data.user.UserResponse
import com.app.democall.composesample.util.attachment.DownloadCallback
import com.app.democall.composesample.util.attachment.UploadCallbacks
import com.example.zdravlje.data.model.response.chat.ChatInfoPayload
import com.example.zdravlje.data.model.response.chat.Feature
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.messages.ChatMessagesAdapter
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.options.ChatAcceptContactFragment
import com.example.zdravlje.ui.chat.options.ChatSendContactDetailsFragment
import com.example.zdravlje.ui.chatAudioAndVideoCall.DemoCallActivity
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.loadImage
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMessagesBinding


class ChatMessagesFragment(
    private val chatData: ChatListResponseItem? = null
) : Fragment(),
    BottomSheetAddFileFragment.OnBottomSheetAddFileOnClickListener {

    private lateinit var binding: FragmentMessagesBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var chatMessagesAdapter: ChatMessagesAdapter
    private lateinit var curentAvcUser: UserResponse

    private lateinit var parentActivity: ChatMessagesActivity
    var isVideo = false
    private var avatarResource: Int = R.drawable.ic_dr_avatar
    private var userPin: String? = null
    var currentUser: UserInfo? = null
    private var avatarImage: String? = null

    companion object {
        const val REQUEST_CODE_FILE_PICKER = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModels()
        showHideShimmer(true)
        binding.rvChatMessages.visibility = View.GONE
        subscribeToObservers()
        setupUI()
        showBottomSheet()
        mainViewModel.startChat(parentActivity.getChat().id)
        mainViewModel.getStartChat(parentActivity.getChat().id)
    }

    private fun getNonCreatorParticipant(chatItem: ChatListResponseItem): ChatParticipant? {
        return chatItem.participants.firstOrNull { it.sgcoId != chatItem.creatorId }
    }

    private fun getCreatorParticipant(chatItem: ChatListResponseItem): ChatParticipant? {
        return chatItem.participants.firstOrNull { it.sgcoId == chatItem.creatorId }
    }

    fun findParticipantForNonCurrentUser(chatItem: ChatListResponseItem): ChatParticipant? {
        val otherParticipant =
            chatItem.participants.firstOrNull { it.sgcoId != currentUser?.sgco_id }
        return otherParticipant
    }

    private fun showBottomSheet() {
        val paramValue = arguments?.getBoolean("deletedBottomSheet") ?: false
        if (paramValue) {
            val bottomSheetDeleteContact = BottomSheetDeleteContact(chatData!!)
            bottomSheetDeleteContact.setTargetFragment(this, 0)
            bottomSheetDeleteContact.show(
                requireActivity().supportFragmentManager,
                bottomSheetDeleteContact.tag
            )
        }
    }

    private fun setupViewModels() {
        val activity = requireActivity() as ChatMessagesActivity
        mainViewModel = activity.getMainViewModel()
        chatViewModel = activity.getChatViewModel()
        dataStoreRepository = activity.getDataStoreRepository()

        val profileRepository = ProfileRepository()
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        mainViewModel.getSdkUser()
        parentActivity = activity as ChatMessagesActivity
        if (context is ChatMessagesActivity) {
            if (parentActivity.getChat().id != null) {
                parentActivity.getChat().id?.let {
                    val sortedChatIds = it.toSortedSet()
                        .sortedDescending()
                    mainViewModel.getMessages(sortedChatIds.toString())
                }
            } else {
                if (chatData?.id != null) {
                    val chat = parentActivity.getChat()
                    chat.id.get(chatData.id.toInt())
                    chat.id.let { chatIdValue ->
                        mainViewModel.getMessages(chatIdValue)
                    }
                } else {
                    Log.e("getMessages", "Chat id is null, unable to fetch messages.")
                }
            }


            binding.chatName.text = parentActivity.getChat().title

            if (!parentActivity.getChat().payload.isNullOrEmpty()) {
                val gson = Gson()
                val chatInfoPayload =
                    gson.fromJson(parentActivity.getChat().payload, ChatInfoPayload::class.java)

                avatarResource = when (chatInfoPayload.chat_type) {
                    100, 101, 102 -> R.drawable.ic_male_doctor_avatar
                    110, 111, 112 -> R.drawable.ic_pharmacy_avatar
                    120, 122 -> R.drawable.ic_male_doctor_avatar
                    121 -> {
                        if (!getNonCreatorParticipant(parentActivity.getChat())?.feature.isNullOrEmpty()) {
                            val gson = Gson()
                            val featureData = gson.fromJson(
                                getNonCreatorParticipant(parentActivity.getChat())?.feature,
                                Feature::class.java
                            )

                            if (featureData != null) {
                                when (featureData.gender) {
                                    "2" -> R.drawable.ic_female_avatar
                                    "1" -> R.drawable.ic_male_avatar
                                    else -> R.drawable.ic_contact_avatar
                                }
                            } else {
                                R.drawable.ic_contact_avatar
                            }
                        } else {
                            R.drawable.ic_contact_avatar
                        }
                    }

                    902 -> R.drawable.ic_group_chat_avatar
                    901 -> R.drawable.ic_group_chat_avatar
                    else -> R.drawable.ic_contact_avatar
                }

                when (chatInfoPayload.chat_type) {
                    999 -> {
                        binding.chatName.text = getString(R.string.chat_inbox_delete_contact)
                        binding.ivParticipantPhoto.setImageResource(R.drawable.ic_contact_avatar)
                    }

                    902 -> {
                        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { response ->
                            response?.body()?.let { user ->
                                currentUser = user

                                val chatItem = parentActivity.getChat()
                                val otherParticipants =
                                    chatItem.participants.filter { it.sgcoId != currentUser!!.sgco_id }

                                chatMessagesAdapter.otherParticipants = otherParticipants
                                chatMessagesAdapter.notifyDataSetChanged()
                            }
                        }
                    }

                    120 -> {
                        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { response ->
                            response?.body()?.let { user ->
                                currentUser = user

                                val chatItem = parentActivity.getChat()
                                val participant = findParticipantForNonCurrentUser(chatItem)

                                binding.chatName.text = participant?.sgcoName ?: "Nepoznat"

                                participant?.avatar?.let {
                                    binding.ivParticipantPhoto.loadImage(
                                        it,
                                        avatarResource
                                    )
                                }

                                avatarImage = participant?.avatar
                                chatMessagesAdapter.avatarGuest = avatarImage
                                chatMessagesAdapter.notifyDataSetChanged() // Osvežava sve stavke u adapteru
                            }
                        }
                    }

                    else -> {
                        binding.chatName.text = parentActivity.getChat().title
                        binding.ivParticipantPhoto.setImageResource(avatarResource)
                    }
                }
            }

            binding.chatDesc.text =
                parentActivity.getChat().participants.size.toString() + " ucesnika"
            if (parentActivity.getChat().participants.size > 2) {
                binding.ivParticipantPhoto.setImageResource(R.drawable.ic_group_chat_avatar)
            }

            val chatType = parentActivity.getChatType()
            val nonCreator = getNonCreatorParticipant(parentActivity.getChat())
            val creator= getCreatorParticipant(parentActivity.getChat())

            if (chatType == 121) {
                val avatarToSend: Any = if (nonCreator?.avatar?.isNotEmpty() == true) {
                    nonCreator.avatar
                } else {
                    if (!nonCreator?.feature.isNullOrEmpty()) {
                        val gson = Gson()
                        val featureData = gson.fromJson(
                            nonCreator?.feature,
                            Feature::class.java
                        )

                        if (featureData != null) {
                            when (featureData.gender) {
                                "2" -> R.drawable.ic_female_avatar
                                "1" -> R.drawable.ic_male_avatar
                                else -> R.drawable.ic_contact_avatar
                            }
                        } else {
                            R.drawable.ic_contact_avatar
                        }
                    } else {
                        R.drawable.ic_contact_avatar
                    }
                }

                val fragment = ChatSendContactDetailsFragment(null, nonCreator, avatarToSend, parentActivity.getChat().id)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.chatOptionFcv, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            if (chatType == 122) {
                val avatarToSend: String = if (creator?.avatar?.isNotEmpty() == true) {
                    println(creator.avatar)
                    creator.avatar
                } else {
                    R.drawable.ic_female_avatar.toString()
                }

                val fragment = ChatAcceptContactFragment(creator, avatarToSend, parentActivity.getChat().id)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.chatOptionFcv, fragment)
                    .addToBackStack(null)
                    .commit()

            } else if (chatType == 999) {
                binding.ivPhoneCall.visibility = View.GONE
                binding.ivVideoCall.visibility = View.GONE
                binding.ivSearch.visibility = View.GONE
                binding.clEdit.visibility = View.GONE
            }
        }

        chatMessagesAdapter = ChatMessagesAdapter(
            mutableListOf(),
            UserResponse("", "", 0, "", "", "", "", ""),
            ::onMessageClick,
            avatarImage
        )

        binding.rvChatMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatMessages.adapter = chatMessagesAdapter

        binding.ivSendMessage.setOnClickListener {
            val messageText = binding.etChat.text.toString().trim()
            if (messageText.isNotEmpty()) {
                binding.etChat.text.clear()

                mainViewModel.sendMessage(parentActivity.getChat().id, messageText, emptyList())
                scrollToBottom()
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.ivSendAttachment.setOnClickListener {
            binding.ivSendAttachment.setOnClickListener(View.OnClickListener {
                val bottomSheetAddFile = BottomSheetAddFileFragment()
                bottomSheetAddFile.setTargetFragment(this, 0)
                bottomSheetAddFile.show(
                    requireActivity().supportFragmentManager,
                    bottomSheetAddFile.tag
                )
            })
        }

        binding.ivPhoneCall.setOnClickListener(View.OnClickListener {
            val activty = requireActivity() as ChatMessagesActivity
            activty.isVideoCall = false
            activty.callId = parentActivity.getChat().id
            mainViewModel.startCall(parentActivity.getChat().id, false)
        })

        binding.ivVideoCall.setOnClickListener(View.OnClickListener {
            val activty = requireActivity() as ChatMessagesActivity
            activty.isVideoCall = true
            activty.callId = parentActivity.getChat().id
            mainViewModel.startCall(parentActivity.getChat().id, true)
        })


        binding.ivParticipantPhoto.setOnClickListener {
            if (parentActivity.getChat().participants.size > 2) {
                val groupChatOptionsFragment = GroupChatOptionsFragment(parentActivity.getChat())
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.chatOptionFcv, groupChatOptionsFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                val chatAvatarActionsFragment =
                    ChatAvatarActionsFragment(parentActivity.getChat())
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.chatOptionFcv, chatAvatarActionsFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun onMessageClick(message: MessagesResponseItem) {

        val downloadCallback = object : DownloadCallback {
            override fun onProgressUpdate(progress: Int, size: Long) {
                // Implementirajte logiku za ažuriranje napretka preuzimanja
                Log.i("DownloadCallback", "Progress: $progress%, Size: $size bytes")
            }

        }

        message.attachments?.firstOrNull()?.let { mainViewModel.downloadFile(it, downloadCallback) }
    }


    private fun subscribeToObservers() {
        profileViewModel.getUserInformation()

        mainViewModel.deleteChatHistoryResponse.observe(viewLifecycleOwner) { response ->
            Toast.makeText(
                requireContext(),
                getString(R.string.chat_inbox_delete_contact),
                Toast.LENGTH_LONG
            )
            mainViewModel.getMessages(response.chatId)
            requireActivity().finish()
        }

//        chatViewModel.denyContactRequestLiveData.observe(viewLifecycleOwner){
//            mainViewModel.getChatList()
//            binding.progressBarEsick.visibility=View.INVISIBLE
//            requireActivity().finish()
//        }

        mainViewModel.deleteChatHistoryResponseErrorLiveData.observe(viewLifecycleOwner) { response ->
            mainViewModel.getMessages(response.chatId)
        }

        mainViewModel.messagesListResponseLiveData.postValue(arrayListOf())

        mainViewModel.userResponseLiveData.observe(viewLifecycleOwner) { user ->
            curentAvcUser = user
            mainViewModel.messagesListResponseLiveData.observe(viewLifecycleOwner) { messageList ->
                showHideShimmer(false)
                binding.rvChatMessages.visibility = View.VISIBLE
                val reversedList = messageList.reversed()
                chatMessagesAdapter.updateEntireMessagesList(reversedList, user)
                chatMessagesAdapter.notifyDataSetChanged()
                scrollToBottom()
            }
        }



        mainViewModel.singleMessageLiveData.observe(viewLifecycleOwner) { messageResponseItem ->
            showHideShimmer(false)
            binding.rvChatMessages.visibility = View.VISIBLE
            chatMessagesAdapter.addMessage(messageResponseItem)
            scrollToBottom()
        }

        mainViewModel.joinCallLiveData.observe(viewLifecycleOwner) { event ->

            // Procesiranje događaja
            val context = requireContext()
            val url = event.url
            val token = event.token
            val isVideo = isVideo
            val chatId = parentActivity.getChat().id
            val intent = DemoCallActivity.newIntent(
                context = context,
                url = url,
                token = token,
                isVideo = isVideo,
                chatId = chatId,
            )

            startActivity(intent)
        }

    }

    private fun scrollToBottom() {
        binding.rvChatMessages.scrollToPosition(chatMessagesAdapter.itemCount - 1)
    }

    //SEND FILE
    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"  // Allow any file type. Adjust as needed.
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select a file"),
            REQUEST_CODE_FILE_PICKER
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                parentActivity = activity as ChatMessagesActivity
                parentActivity.getChat().id?.let { chatId ->
                    sendFile(uri, chatId)
                }
            }
        }
    }

    private fun sendFile(uri: Uri, chatId: String) {

        mainViewModel.uploadFile(uri, chatId, object : UploadCallbacks {

            override fun onAttachmentCreated(attachment: AttachmentRequest) {

                mainViewModel.sendAttachment(chatId, attachment)
                // Handle attachment creation
                Log.i(
                    "sendFile",
                    "attachment: id " + attachment.id + " name " + attachment.name + " size " + attachment.size + " type" + attachment.type
                )

            }

            override fun onProgressUpdate(size: Int) {
                // Handle progress update
                Log.i("sendFile", "size" + size.toString())
            }
        })
    }

    override fun onResume() {
        super.onResume()
        parentActivity.getChat().id?.let { mainViewModel.getMessages(it) }
    }

    override fun onOnBottomSheetAddFileOnClick() {
        chooseFile()
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }
}
