package com.example.zdravlje.ui.adaptersAndViewHolders.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.democall.composesample.data.chat.list.ChatListResponseItem
import com.example.zdravlje.data.model.response.chat.ChatInfoPayload
import com.example.zdravlje.data.model.response.chat.Feature
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.loadImage
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ItemChatInboxBinding
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatInboxAdapter(
    private val chatList: ArrayList<ChatListResponseItem>,
    private val onChatItemSelected: (chatItem: ChatListResponseItem, chatType: Int) -> Unit,
    context: Context,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<ChatInboxAdapter.ChatInboxViewHolder>() {

    private val gson = Gson()
    private val profileViewModel: ProfileViewModel
    private var userData: UserInfo? = null

    init {
        val dataStoreRepository = DataStoreRepository(context)
        val profileRepository = ProfileRepository()
        val viewModelFactory = ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(context as ViewModelStoreOwner, viewModelFactory)
            .get(ProfileViewModel::class.java)

        profileViewModel.userInfoLiveData.observe(lifecycleOwner) { userInfo ->
            userData = userInfo.body()
        }

        profileViewModel.getUserInformation()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatInboxViewHolder {
        val binding =
            ItemChatInboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatInboxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatInboxViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatInboxViewHolder(private val binding: ItemChatInboxBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: ChatListResponseItem) = with(binding) {
            val payload = chatItem.payload?.let { gson.fromJson(it, ChatInfoPayload::class.java) }
            val chatType = payload?.chat_type ?: 0

            val displayName = when {
                chatType == 901 || chatType == 902 -> chatItem.title
                chatType == 999 -> ContextCompat.getString(
                    itemView.context,
                    R.string.chat_inbox_delete_contact
                )

                chatItem.participants.size == 1 -> getCreator(chatItem)?.sgcoName
                chatItem.participants.size < 2 -> getNonCreator(chatItem)?.sgcoName
                payload != null -> getDisplayNameByType(payload.chat_type, chatItem)
                else -> chatItem.title
            } ?: chatItem.title

            tvUserTitle.text = displayName

            val previewText = getPreviewText(itemView.context, chatType)
            tvChatSubtitle.text = previewText ?: chatItem.lastMessage?.text.orEmpty()
            tvChatTime.text = createDate(payload?.last_message_at ?: chatItem.createdAt)

            val avatar = getAvatarResource(chatType, chatItem)
            setImage(chatType, chatItem, avatar)

            root.setOnClickListener {
                onChatItemSelected(chatItem, chatType)
            }

            //unread messages
            if (payload?.unread_message_count!! > 0) {
                binding.mcvUnreadMessages.visibility = View.VISIBLE
                binding.tvUnreadMessagesValue.text = payload.unread_message_count.toString()
                binding.tvChatTime.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.base_blue_color
                    )
                )
            } else if (isPendingRequest(chatType)) {
                binding.mcvUnreadMessages.visibility = View.VISIBLE
                binding.tvUnreadMessagesValue.text = "1"
                binding.tvChatTime.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.base_blue_color
                    )
                )
            } else {
                binding.mcvUnreadMessages.visibility = View.GONE
            }
        }

        private fun getDisplayNameByType(type: Int, chatItem: ChatListResponseItem): String? {
            return when (type) {
                121 -> getNonCreator(chatItem)?.sgcoName
                122 -> getCreator(chatItem)?.sgcoName
                120 -> getOtherParticipant(chatItem)?.sgcoName
                else -> chatItem.title
            }
        }

        private fun setImage(type: Int, chatItem: ChatListResponseItem, placeholder: Int) {
            val avatarUrl = when (type) {
                121 -> getNonCreator(chatItem)?.avatar
                122 -> getCreator(chatItem)?.avatar
                120 -> getOtherParticipant(chatItem)?.avatar
                else -> null
            }

            avatarUrl?.let {
                binding.ivParticipantPhoto.loadImage(it, placeholder)
            } ?: binding.ivParticipantPhoto.setImageResource(placeholder)

            if (type == 121 || type == 122) {
                binding.tvChatSubtitle.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.base_blue_color)
                )
            }
        }

        private fun createDate(timestamp: Int): String {
            val messageDate = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp.toLong()), ZoneId.systemDefault()
            )
            val currentDate = LocalDate.now()

            return when (messageDate.toLocalDate()) {
                currentDate -> messageDate.format(DateTimeFormatter.ofPattern("HH:mm"))
                currentDate.minusDays(1) -> itemView.context.getString(R.string.chat_inbox_adapter_yesterday)
                else -> messageDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }
        }
    }

    fun updateAdapter(newChats: List<ChatListResponseItem>) {
        val diffResult = DiffUtil.calculateDiff(ChatDiffCallback(chatList, newChats))
        chatList.clear()
        chatList.addAll(newChats)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getAvatarResource(chatType: Int, chatItem: ChatListResponseItem): Int {
        return when (chatType) {
            100, 101, 102, 120, 122 -> R.drawable.ic_male_doctor_avatar
            110, 111, 112 -> R.drawable.ic_pharmacy_avatar
            121 -> {
                getNonCreator(chatItem)?.feature?.let {
                    when (gson.fromJson(it, Feature::class.java).gender) {
                        "1" -> R.drawable.ic_male_avatar
                        "2" -> R.drawable.ic_female_avatar
                        else -> R.drawable.ic_contact_avatar
                    }
                } ?: R.drawable.ic_contact_avatar
            }

            901, 902 -> R.drawable.ic_group_chat_avatar
            else -> R.drawable.ic_contact_avatar
        }
    }

    private fun getCreator(chatItem: ChatListResponseItem) =
        chatItem.participants.firstOrNull { it.sgcoId == chatItem.creatorId }

    private fun getNonCreator(chatItem: ChatListResponseItem) =
        chatItem.participants.firstOrNull { it.sgcoId != chatItem.creatorId }

    private fun getOtherParticipant(chatItem: ChatListResponseItem) =
        chatItem.participants.firstOrNull { it.sgcoId != userData?.sgco_id }

    class ChatDiffCallback(
        private val oldList: List<ChatListResponseItem>,
        private val newList: List<ChatListResponseItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

    fun getPreviewText(context: Context, chatType: Int): String? {
        val pendingRequests = listOf(102, 112, 122)
        val sentRequests = listOf(101, 111, 121)

        return when {
            pendingRequests.contains(chatType) -> ContextCompat.getString(
                context,
                R.string.chat_inbox_adapter_new_contact
            )

            sentRequests.contains(chatType) -> ContextCompat.getString(
                context,
                R.string.chat_inbox_adapter_send_request
            )

            else -> null
        }
    }

    fun isPendingRequest(chatType: Int): Boolean {
        val pendingRequests = listOf(102, 112, 122)
        return chatType in pendingRequests
    }

}
