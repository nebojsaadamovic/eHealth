package com.example.zdravlje.ui.chat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.ui.adaptersAndViewHolders.chat.ChatInboxAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.chat.options.ChatOptionsActivity
import com.example.zdravlje.ui.chat.singleAndGroupMessages.ChatMessagesActivity
import com.example.zdravlje.ui.dialogs.ChatListFilterDialog
import com.example.zdravlje.ui.home.HomeActivity
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.notification.NotificationActivity
import com.example.zdravlje.ui.notification.NotificationViewModel
import com.example.zdravlje.ui.notification.NotificationViewModelFactory
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.DateUtils
import com.example.zdravlje.utilities.loadImage
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChatInboxBinding

class ChatInboxFragment : BaseFragment(), ChatListFilterDialog.IChatFilterOptions,
    PinChatBottomSheetFragment.OnPinChatListener {

    private lateinit var binding: FragmentChatInboxBinding
    private lateinit var chatInboxAdapter: ChatInboxAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var notificationViewModel: NotificationViewModel

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var userPin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideShimmer(true)
        observeChatList()
        subscribeToObservers()
        setupUI()
        notificationCount()
        //backPressFromSystem()
    }

    private fun setViewModels() {
        val homeActivity = requireActivity() as HomeActivity
        mainViewModel = homeActivity.getMainViewModel()
        dataStoreRepository = homeActivity.getDataStoreRepository()

        chatViewModel = homeActivity.getChatViewModel()

        val notificationRepository = NotificationRepository()
        val notificationViewModelFactory =
            NotificationViewModelFactory(notificationRepository, dataStoreRepository)
        notificationViewModel = ViewModelProvider(requireActivity(), notificationViewModelFactory)
            .get(NotificationViewModel::class.java)

        val profileRepository = ProfileRepository()
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)
    }

    private fun subscribeToObservers() {
        if (isPinSet()) {
            binding.wrapper.visibility = View.GONE
        }else{
            binding.wrapper.visibility = View.VISIBLE
        }

        userPin = getStoredPin()
        notificationCount()

        observeChatList()

        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            val userData = userInfo.body()

            val avatarValue = userData?.avt_encoded_value
            val fullName = userData?.sgco_name ?: ""

            if (!avatarValue.isNullOrEmpty()) {
                binding.ivAvatar.visibility = View.VISIBLE
                binding.tvAvatarInitials.visibility = View.GONE
                binding.ivAvatar.loadImage(
                    avatarValue,
                    R.drawable.ic_contact_avatar
                )
            } else {
                if (userData?.pat_sex == 2) {
                    binding.tvAvatarInitials.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.screeningColor)
                    )
                } else {
                    binding.tvAvatarInitials.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                    )
                }

                binding.ivAvatar.visibility = View.GONE
                binding.tvAvatarInitials.visibility = View.VISIBLE
                binding.tvAvatarInitials.text = CommonUtils.getInitials(fullName)
            }
        }
    }

    private fun notificationCount() {
        notificationViewModel.notificationCount(
            NotificationReadRequest(
                pin = userPin,
                currentTime = DateUtils.getCurrentTimeyyyyMMddHHmm(),
                unread = 0
            )
        )
        notificationViewModel.notificationCountLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response != null) {
                    if (response.isSuccessful) {
                        response.body()?.let { responseData ->
                            val total = responseData.total
                            if (total != null && total != 0) {
                                binding.mtvNotificationCount.text = total.toString()
                            } else {
                                binding.ivNotification.setImageResource(R.drawable.iv_bell)
                            }
                        }
                    }
                }
            }
        )
    }

    private fun observeChatList() {
        mainViewModel.chatListResponseLiveData.observe(viewLifecycleOwner) { chatList ->
            showHideShimmer(false)
            if (chatList.data.size > 0) {
                binding.emptyScreen.emptyScreenWrapper.visibility = View.GONE
            } else {
                binding.emptyScreen.emptyScreenWrapper.visibility = View.VISIBLE
            }
            chatInboxAdapter.updateAdapter(chatList.data)
            binding.chatListRecyclerView.layoutManager?.scrollToPosition(0)
        }

        chatViewModel.cancelFriendRequestLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(requireContext(), "Otkazan zahtev $response", Toast.LENGTH_LONG).show()
            }
        }
        chatViewModel.cancelFriendRequestErrorLiveData.observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireContext(), "Greska $response", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setupUI() {

        binding.chatListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        chatInboxAdapter = ChatInboxAdapter(
            chatList = arrayListOf(),
            onChatItemSelected = { chatItem, chatType ->
                val gson = Gson()
                val chatListResponseItemJson = gson.toJson(chatItem)

                val intent = Intent(requireActivity(), ChatMessagesActivity::class.java).apply {
                    putExtra("chat_list_response_item_json", chatListResponseItemJson)
                    putExtra("chat_type", chatType)
                }

                startActivity(intent)
            },
            context = requireContext() ,
            lifecycleOwner = viewLifecycleOwner
        )

        binding.chatListRecyclerView.adapter = chatInboxAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            mainViewModel.getChatList(title = binding.etSearch.text.toString())
        }

        binding.fab.setOnClickListener {
            val intent = Intent(requireActivity(), ChatOptionsActivity::class.java)
            startActivity(intent)
        }

        binding.ivFilter.setOnClickListener {
            val chatSettingsDialog = ChatListFilterDialog(requireContext(), this)
            chatSettingsDialog.setCanceledOnTouchOutside(true)
            chatSettingsDialog.show()
        }

        binding.ivNotification.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }
            }

            override fun afterTextChanged(p0: Editable?) {
                val searchText = p0?.toString() ?: ""

                searchRunnable = Runnable {
                    mainViewModel.getChatList(title = searchText)
                }
                handler.postDelayed(searchRunnable!!, 500)
            }
        })

        val mbLogInButton = binding.wrapper.findViewById<MaterialButton>(R.id.mbLogIn)
        mbLogInButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })

        val mbEnterPinCode = binding.wrapper.findViewById<MaterialButton>(R.id.mbEnterPinCode)

        mbEnterPinCode.setOnClickListener {
            val pinChatBottomSheetFragment = PinChatBottomSheetFragment()
            pinChatBottomSheetFragment.setOnPinChatListener(this)
            pinChatBottomSheetFragment.show(parentFragmentManager, pinChatBottomSheetFragment.tag)
        }

        val ivBackButton = binding.wrapper.findViewById<ImageView>(R.id.ivBack)
        ivBackButton.setOnClickListener(View.OnClickListener {
           requireActivity().onBackPressed()
        })
    }


    override fun onShowDoctorsFilterDialog() {
        showHideShimmer(true)
        mainViewModel.getChatList(150)
    }

    override fun onShowPharmacistsFilterDialog() {
        showHideShimmer(true)
        mainViewModel.getChatList(userType = 160)
    }

    override fun onShowMyContactsFilterDialog() {
        showHideShimmer(true)
        mainViewModel.getChatList(userType = 180, chatType = 1)
    }

    override fun onShowChatGroupsFilterDialog() {
        showHideShimmer(true)
        mainViewModel.getChatList(chatType = 2)
    }

    override fun onShowRequestsReceivedFilterDialog() {
        showHideShimmer(true)
        mainViewModel.getChatList(connectionType = 3)
    }

    override fun onShowAllFilterDialog() {
        showHideShimmer(true)
        mainViewModel.getChatList()
    }

    override fun onPinChatChange() {
        println("aaaaaaawwwzzzzzzzzzzzz")
        binding.wrapper.visibility = View.GONE
    }

    private fun showHideShimmer(boolean: Boolean){
        if(boolean==true){
            binding.shimmer.visibility = View.VISIBLE
        }else{
            binding.shimmer.visibility = View.GONE
        }
    }
}
