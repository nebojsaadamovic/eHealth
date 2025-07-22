package com.example.zdravlje.ui.adaptersAndViewHolders.chat.messages

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.democall.composesample.data.chat.ChatParticipant
import com.app.democall.composesample.data.chat.messages.MessagesResponseItem
import com.app.democall.composesample.data.user.UserResponse
import com.example.zdravlje.utilities.DateUtils.formatTimestampToDate
import com.example.zdravlje.utilities.DateUtils.formatTimestampToHoursAndMinutes
import com.example.zdravlje.utilities.loadImage
import com.nites.ezdravljeapp.R

enum class MessageType {
    SYSTEM,
    USER,
    OTHER_USER,
    USER_IMAGE,
    OTHER_USER_IMAGE,
    USER_DOCUMENT,
    OTHER_USER_DOCUMENT
}

class ChatMessagesAdapter(
    private var messageList: MutableList<MessagesResponseItem>,
    private var user: UserResponse,
    private val itemClickListener: (MessagesResponseItem) -> Unit,
    var avatarGuest: String? = null,
    var otherParticipants: List<ChatParticipant>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_RECEIVER = 1
        const val VIEW_TYPE_SENDER = 2
        const val VIEW_TYPE_DATE = 3
        const val VIEW_TYPE_SYSTEM = 4
        const val VIEW_TYPE_USER_IMAGE = 5
        const val VIEW_TYPE_OTHER_USER_IMAGE = 6
        const val VIEW_TYPE_USER_DOCUMENT = 7
        const val VIEW_TYPE_OTHER_USER_DOCUMENT = 8
    }

    private inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: MessagesResponseItem, showNameAndAvatar: Boolean) {

            itemView.findViewById<TextView>(R.id.tvSingleMessage).text = message.text
            itemView.findViewById<TextView>(R.id.tvTimeReceivedMessage).text =
                formatTimestampToHoursAndMinutes(message.createdAt)

            showAndHideUserName(itemView, showNameAndAvatar, message)
        }

    }


    private inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: MessagesResponseItem) {
            itemView.findViewById<TextView>(R.id.tvSingleMessage).text = message.text
            itemView.findViewById<TextView>(R.id.tvTimeReceivedMessage).text =
                formatTimestampToHoursAndMinutes(message.createdAt)
        }
    }

    private inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(date: String) {
            itemView.findViewById<TextView>(R.id.chatTime).text = date
        }
    }

    // Dodajemo ViewHoldere za različite tipove poruka
    private inner class SystemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: MessagesResponseItem) {
            itemView.findViewById<TextView>(R.id.tvSystemMessage).text = message.text
            itemView.findViewById<TextView>(R.id.tvTimeReceivedMessage).text =
                formatTimestampToHoursAndMinutes(message.createdAt)
        }
    }

    private inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: MessagesResponseItem) {
            setTextAndTime(message, itemView)
            setDocumentSize(message, itemView)
            itemView.setOnClickListener(View.OnClickListener {
                itemClickListener(message)
            })
        }
    }

    private inner class OtherImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: MessagesResponseItem, showNameAndAvatar: Boolean) {
            // Implementacija bind metoda za slike
            showAndHideUserName(itemView, showNameAndAvatar, message)
            setTextAndTime(message, itemView)
            setDocumentSize(message, itemView)
            itemView.setOnClickListener(View.OnClickListener {
                itemClickListener(message)
            })
        }
    }

    private inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: MessagesResponseItem) {
            setTextAndTime(message, itemView)
            setDocumentSize(message, itemView)
            itemView.setOnClickListener(View.OnClickListener {
                itemClickListener(message)
            })
        }
    }

    private inner class OtherDocumentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(message: MessagesResponseItem, showNameAndAvatar: Boolean) {
            // Implementacija bind metoda za dokumente
            showAndHideUserName(itemView, showNameAndAvatar, message)
            setTextAndTime(message, itemView)
            setDocumentSize(message, itemView)
            itemView.setOnClickListener(View.OnClickListener {
                itemClickListener(message)
            })
        }
    }

    private fun showAndHideUserName(
        itemView: View,
        showNameAndAvatar: Boolean,
        message: MessagesResponseItem,
    ) {
        val userNameTextView = itemView.findViewById<TextView>(R.id.userName)
        val avatar = itemView.findViewById<ImageView>(R.id.ivAvatar)
        userNameTextView.visibility = if (showNameAndAvatar) View.VISIBLE else View.GONE
        avatar.visibility = if (showNameAndAvatar) View.VISIBLE else View.INVISIBLE

        if (!avatarGuest.isNullOrEmpty()) {
            avatar.loadImage(
                avatarGuest!!,
                R.drawable.ic_contact_avatar
            )
        } else {
            avatar.setImageResource(R.drawable.ic_contact_avatar)
        }

        if (showNameAndAvatar) {
            otherParticipants?.forEach { person ->

                if (person.sgcoId == message.senderId.toInt()) {
                    avatarGuest = person.avatar
                    avatar.loadImage(
                        avatarGuest!!,
                        R.drawable.ic_contact_avatar
                    )
                }
            }

            userNameTextView.text = message.senderName
        }

    }

    private fun setTextAndTime(message: MessagesResponseItem, itemView: View) {
        val documentDesc = itemView.findViewById<TextView>(R.id.tvSingleMessage)
        documentDesc.text = message.attachments?.firstOrNull()?.name
        itemView.findViewById<TextView>(R.id.tvTimeReceivedMessage).text =
            formatTimestampToHoursAndMinutes(message.createdAt)
    }

    private fun setDocumentSize(message: MessagesResponseItem, itemView: View) {

        val attachmentSize = message.attachments?.firstOrNull()?.size
        val documentDesc = itemView.findViewById<TextView>(R.id.size)

        if (attachmentSize != null) {
            if (attachmentSize < 1024 * 1024) {
                // Prikaz u KB
                val sizeInKB = attachmentSize / 1024.0
                documentDesc.text = String.format("%.2f KB", sizeInKB)
            } else {
                // Prikaz u MB
                val sizeInMB = attachmentSize / (1024.0 * 1024.0)
                documentDesc.text = String.format("%.2f MB", sizeInMB)
            }
        } else {
            documentDesc.text = "N/A" // ili bilo koji drugi tekst kada nema priloga
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_RECEIVER -> ReceiverViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_receiver, parent, false)
            )

            VIEW_TYPE_SENDER -> SenderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_sender, parent, false)
            )

            VIEW_TYPE_DATE -> DateViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_time, parent, false)
            )

            VIEW_TYPE_SYSTEM -> SystemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_system_message, parent, false)
            )

            VIEW_TYPE_USER_IMAGE -> ImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image, parent, false)
            )

            VIEW_TYPE_OTHER_USER_IMAGE -> OtherImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_other_image, parent, false)
            )

            VIEW_TYPE_USER_DOCUMENT -> DocumentViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_document, parent, false)
            )

            VIEW_TYPE_OTHER_USER_DOCUMENT -> OtherDocumentViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_other_document, parent, false)
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        val showNameAndAvatar = shouldShowName(position)

        when (holder.itemViewType) {
            VIEW_TYPE_SENDER -> (holder as SenderViewHolder).bind(message)
            VIEW_TYPE_RECEIVER -> {
                (holder as ReceiverViewHolder).bind(message, showNameAndAvatar)
            }

            VIEW_TYPE_DATE -> (holder as DateViewHolder).bind(formatTimestampToDate(message.createdAt))
            VIEW_TYPE_SYSTEM -> (holder as SystemViewHolder).bind(message)
            VIEW_TYPE_USER_IMAGE -> (holder as ImageViewHolder).bind(message)
            VIEW_TYPE_OTHER_USER_IMAGE -> (holder as OtherImageViewHolder).bind(
                message,
                showNameAndAvatar
            )

            VIEW_TYPE_USER_DOCUMENT -> (holder as DocumentViewHolder).bind(message)
            VIEW_TYPE_OTHER_USER_DOCUMENT -> (holder as OtherDocumentViewHolder).bind(
                message,
                showNameAndAvatar
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messageResponseItem = messageList[position]
        return when {
            isDateCard(position) -> VIEW_TYPE_DATE
            determineMessageType(messageResponseItem) == MessageType.SYSTEM -> VIEW_TYPE_SYSTEM
            determineMessageType(messageResponseItem) == MessageType.USER -> VIEW_TYPE_SENDER
            determineMessageType(messageResponseItem) == MessageType.OTHER_USER -> VIEW_TYPE_RECEIVER
            determineMessageType(messageResponseItem) == MessageType.USER_IMAGE -> VIEW_TYPE_USER_IMAGE
            determineMessageType(messageResponseItem) == MessageType.OTHER_USER_IMAGE -> VIEW_TYPE_OTHER_USER_IMAGE
            determineMessageType(messageResponseItem) == MessageType.USER_DOCUMENT -> VIEW_TYPE_USER_DOCUMENT
            determineMessageType(messageResponseItem) == MessageType.OTHER_USER_DOCUMENT -> VIEW_TYPE_OTHER_USER_DOCUMENT
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    private fun determineMessageType(chatMessage: MessagesResponseItem): MessageType {
        return when (chatMessage.senderId) {
            "0" -> MessageType.SYSTEM
            user.sgcoid.toString() -> determineUserMessageType(chatMessage, isLoggedInUser = true)
            else -> determineUserMessageType(chatMessage, isLoggedInUser = false)
        }
    }

    private fun determineUserMessageType(
        chatMessage: MessagesResponseItem,
        isLoggedInUser: Boolean
    ): MessageType {
        return if (chatMessage.text.isNotEmpty()) {
            if (isLoggedInUser) MessageType.USER else MessageType.OTHER_USER
        } else {
            val attachmentType = chatMessage.attachments?.firstOrNull()?.type
            if (attachmentType != null) {
                Log.i("docType", attachmentType)
            }
            when (attachmentType) {
                "jpg", "png", "jpeg", "image/jpg", "image/png", "image/jpeg", "application/image" -> if (isLoggedInUser) MessageType.USER_IMAGE else MessageType.OTHER_USER_IMAGE
                "pdf", "docx", "doc", "application/pdf", "application/docx", "application/doc" -> if (isLoggedInUser) MessageType.USER_DOCUMENT else MessageType.OTHER_USER_DOCUMENT
                else -> if (isLoggedInUser) MessageType.USER_DOCUMENT else MessageType.OTHER_USER_DOCUMENT
            }
        }
    }

    private fun isDateCard(position: Int): Boolean {
        if (position == 0) return true
        val currentMessage = messageList[position]
        val previousMessage = messageList[position - 1]
        val currentMessageDate = formatTimestampToDate(currentMessage.createdAt)
        val previousMessageDate = formatTimestampToDate(previousMessage.createdAt)
        return currentMessageDate != previousMessageDate
    }

    private fun shouldShowName(position: Int): Boolean {
        if (position == 0) return true
        val currentMessage = messageList[position]
        val previousMessage = messageList[position - 1]
        return currentMessage.senderId != previousMessage.senderId
    }

    fun updateEntireMessagesList(list: List<MessagesResponseItem>, userResponse: UserResponse) {
        user = userResponse
        messageList.clear()
        val newList = mutableListOf<MessagesResponseItem>()

        for (i in list.indices) {
            if (i == 0 || formatTimestampToDate(list[i].createdAt) != formatTimestampToDate(list[i - 1].createdAt)) {
                val dateMessage = MessagesResponseItem(
                    attachments = emptyList(),
                    createdAt = list[i].createdAt,
                    id = "",
                    senderId = "",
                    senderName = "",
                    text = "",
                    chatId = "",
                    type = "",
                    anonymous = false
                )
                newList.add(dateMessage)
            }
            newList.add(list[i])
        }

        messageList.addAll(newList)
        notifyDataSetChanged()
    }

    fun addMessage(message: MessagesResponseItem) {
        // Provera da li treba dodati datum pre novog itema
        if (messageList.isNotEmpty()) {
            val lastMessage = messageList.last()
            if (formatTimestampToDate(lastMessage.createdAt) != formatTimestampToDate(message.createdAt)) {
                val dateMessage = MessagesResponseItem(
                    attachments = emptyList(),
                    createdAt = message.createdAt,
                    id = "",
                    senderId = "",
                    senderName = "",
                    text = "",
                    chatId = "",
                    type = "",
                    anonymous = false
                )
                messageList.add(dateMessage)
            }
        }
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }
}
