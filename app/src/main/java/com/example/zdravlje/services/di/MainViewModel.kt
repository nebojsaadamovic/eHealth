package com.example.zdravlje.services.di

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.app.democall.composesample.data.call.IncomingCallResponse
import com.app.democall.composesample.data.call.JoinCallResponse
import com.app.democall.composesample.data.chat.Attachment
import com.app.democall.composesample.data.chat.AttachmentRequest
import com.app.democall.composesample.data.chat.ChatResponse
import com.app.democall.composesample.data.chat.create.CreateChatResponse
import com.app.democall.composesample.data.chat.delete.DeleteChatHistoryResponse
import com.app.democall.composesample.data.chat.list.ChatListResponse
import com.app.democall.composesample.data.chat.messages.MessageRequest
import com.app.democall.composesample.data.chat.messages.MessagesResponse
import com.app.democall.composesample.data.chat.messages.MessagesResponseItem
import com.app.democall.composesample.data.chat.modify.ModifyChatResponse
import com.app.democall.composesample.data.notification.Notification
import com.app.democall.composesample.data.user.UserResponse
import com.app.democall.composesample.util.attachment.AttachmentManager
import com.app.democall.composesample.util.attachment.DownloadCallback
import com.app.democall.composesample.util.attachment.UploadCallbacks
import com.app.democall.composesample.util.observers.network.ConnectivityObserver
import com.app.democall.composesample.util.observers.network.ConnectivityObserver.Status
import com.app.democall.extensions.hide
import com.app.democall.sdk.call.api.AvcSdkCall
import com.app.democall.sdk.chat.api.AvcSdkChat
import com.app.democall.sdk.manager.api.AvcSdkManager
import com.app.democall.sdk.user.api.AvcSdkUser
import com.app.democall.socket.SocketManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val avcSdkManager: AvcSdkManager,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val avcSdkChat: AvcSdkChat = avcSdkManager.container.chatSdk
    private val avcSdkUser: AvcSdkUser = avcSdkManager.container.userSdk
    private val avcSdkAttachment: AttachmentManager = avcSdkManager.container.attachmentManager
    private val callSdk: AvcSdkCall = avcSdkManager.container.callSdk
    private val socketManager: SocketManager = avcSdkManager.container.socketManager;

    private var chatId = ""


    var chatListResponseLiveData: MutableLiveData<ChatListResponse> = MutableLiveData()

    var messagesListResponseLiveData: MutableLiveData<ArrayList<MessagesResponseItem>> =
        MutableLiveData()

    var userResponseLiveData: MutableLiveData<UserResponse> = MutableLiveData()

    var singleMessageLiveData: MutableLiveData<MessagesResponseItem> = MutableLiveData()

    var joinCallMutableLiveData: MutableLiveData<JoinCallResponse> = MutableLiveData()

    val joinCallLiveData = joinCallMutableLiveData.hide().distinctUntilChanged()

    var createChatResponseLiveData: MutableLiveData<CreateChatResponse> = MutableLiveData()
    var deleteChatHistoryResponse: MutableLiveData<DeleteChatHistoryResponse> = MutableLiveData()
    var deleteChatHistoryResponseErrorLiveData: MutableLiveData<DeleteChatHistoryResponse> =
        MutableLiveData()

    var startChatResponseLiveData: MutableLiveData<ChatResponse> = MutableLiveData()
    var startChatResponseErrorLiveData: MutableLiveData<ChatResponse> =
        MutableLiveData()

    private val handler = CoroutineExceptionHandler { c, t ->
        Log.e("MainViewModelError", t.toString())
        Log.e("MainViewModelError", c.toString())
    }

    private val user = avcSdkUser.userFlow()
        .catch {
            Log.e("error", it.toString())
        }.shareIn(viewModelScope, SharingStarted.Eagerly)

    val userName = user.map { it.firstName }

    private val _showInternetConnectionError = MutableStateFlow(false)
    val showInternetConnectionError = _showInternetConnectionError.asStateFlow()

    fun hideInternetError() = viewModelScope.launch {
        _showInternetConnectionError.emit(false)
    }

    init {

        viewModelScope.launch {
            avcSdkManager.events.collectLatest {
                when (it) {
                    is UserResponse -> {
                        userResponseLiveData.postValue(it)
                    }

                    is ChatListResponse -> {
                        Log.i("ttasd12121", "Chat")
                    }

                    is MessagesResponse -> {}
                    is ChatResponse -> {
                        Log.i("PRPRPRA", "ChatResponse1122 " + it.toString())

                    }

                    is IncomingCallResponse -> {}
                    is MessagesResponseItem -> {
                        Log.i("ttasd12121", "usao")
                        getChatList()
                    }
                }
            }
        }

        observeNetwork()
        avcSdkManager.events.onEach {
            println(it)
            Log.i("eventFromSdk", it.toString())
        }.launchIn(viewModelScope)

        getSdkUser()
        listenMessagesResponse()
        listenChatListResponse()
        listenMessagesResponseItem()
        listenCreateChatResponse()
        listenModifiedChat()
        listenDeletedChatHistory()
        listenChatInfo()
        listenStartChat()

        listenChatListResponse1()

    }

    fun startSdk(
        token: String,
        langCode: String,
        firebaseToken: String,
        deviceType: String
    ) {
        viewModelScope.launch(handler) {
            avcSdkManager.start(
                token,
                langCode,
                firebaseToken,
                deviceType
            )

        }
    }

    // LISTEN EVENTS
    private fun listenCreateChatResponse() {
        socketManager.listenEvent(CreateChatResponse, CreateChatResponse::class.java) {
            createChatResponseLiveData.postValue(it)
            getChatList()
        }
    }

    private fun listenChatListResponse() {
        socketManager.listenEvent(ChatListResponse, ChatListResponse::class.java) {
            Log.i("listenChatListResponse", "listenChatListResponse " + it.data.toString())
            chatListResponseLiveData.postValue(it);
        }
    }

    private fun listenChatListResponse1() {

//        viewModelScope.launch {
//            avcSdkChat.chatEvents
//                .filterIsInstance<ChatListResponse>()
//                .onEach {
//                    Log.i("listen by events of chatEvents", it.data.toString())
//                    chatListResponseLiveData.postValue(it);
//                }
//                .collect()
//        }

        viewModelScope.launch {
            avcSdkChat.chatEvents
                .collectLatest {
                    when (it) {
                        is ChatListResponse -> { // do something}
                            chatListResponseLiveData.postValue(it);
                        }
                    }
                }
        }
    }

    private fun listenMessagesResponse() {
        socketManager.listenEvent(MessagesResponse, MessagesResponse::class.java) {
            messagesListResponseLiveData.postValue(it.data as ArrayList<MessagesResponseItem>?)
            Log.i("PRPRPRA", "listenMessagesResponseItem12 " + it.toString())
            getChatList()


        }
    }

    public fun getMessagesForSomething() {
        getMessages(chatId)
    }

    private fun listenMessagesResponseItem() {
        socketManager.listenEvent(MessagesResponseItem, MessagesResponseItem::class.java) {
            Log.i("PRPRPRA", "listenMessagesResponseItem12 " + it.toString())
            singleMessageLiveData.postValue(it)
            getChatList()
        }
    }

    private fun listenIncomingCall() {
        socketManager.listenEvent(IncomingCallResponse, IncomingCallResponse::class.java) {
            Log.i("PRPRPRA", "listenIncomingCall " + it.toString())
            // acceptCall(it.chatId, it.isVideo)
        }
    }

    private fun listenIsCallAccepted() {
        socketManager.listenEvent(JoinCallResponse, JoinCallResponse::class.java) {
            Log.i("PRPRPRA", "listenIsCallAccepted" + it.toString())
            joinCallMutableLiveData.postValue(it)
        }
    }

    private fun listenModifiedChat() {
        socketManager.listenEvent(ModifyChatResponse, ModifyChatResponse::class.java) { response ->
            Log.i("PRPRPRA", "private fun listenModifiedChat() - response : $response")
        }
    }

    private fun listenChatInfo() {
        socketManager.listenEvent(ChatResponse, ChatResponse::class.java) { response ->
            Log.i("PRPRPRA", "private fun listenChatInfo() - response : $response")
        }
    }

    private fun listenStartChat() {
        socketManager.listenEvent(ChatResponse, ChatResponse::class.java) { response ->
            if (response != null) {
                startChatResponseLiveData.postValue(response)
            } else {
                startChatResponseErrorLiveData.value = response
            }
        }
    }

    private fun listenDeletedChatHistory() {
        socketManager.listenEvent(
            DeleteChatHistoryResponse,
            DeleteChatHistoryResponse::class.java
        ) { response ->
            if (response != null) {
                deleteChatHistoryResponse.postValue(response)
                getChatList()
                getMessages(response.chatId)
            } else {
                Log.e("SocketError", "Failed to delete chat history: Response is null")
            }
        }
    }


    fun startChat(chatId: String) {
        viewModelScope.launch(handler) {
            avcSdkChat.startChat(chatId)
        }
//        socketManager.emit(ChatRequest, ChatRequest(chatId = chatId))
//        Log.i("PRPRPRA", "private fun startChat - chatId : $chatId")
    }

    // END LISTEN EVENTS
    fun getMessages(chatId: String) {
        viewModelScope.launch(handler) {
            avcSdkChat.getMessages(chatId)
        }
    }

    // END LISTEN EVENTS
    fun getStartChat(chatId: String) {
        viewModelScope.launch(handler) {
            avcSdkChat.startChat(chatId)
        }
    }

    fun getChatList(
        userType: Int? = null,
        chatType: Int? = null,
        title: String? = "",
        connectionType: Int? = null
    ) {

        viewModelScope.launch(handler) {
            avcSdkChat.getChatList(
                title = if (title.isNullOrEmpty()) null else title,
                usersType = userType,
                chatType = chatType,
                connectionType = connectionType
            )
        }
    }


    fun sendNotification() {
        viewModelScope.launch(handler) {
            avcSdkChat.sendNotification(
                Notification(
                    title = "Test",
                    message = "Test tst",
                    payload = "",
                    type = 103,
                    receiverId = 10186,
                    senderId = 10128
                )
            )
        }
    }

    fun sendMessage(chatId: String, text: String, attachments: List<AttachmentRequest>) {
        viewModelScope.launch(handler) {
            avcSdkChat.sendMessage(chatId, text, type = "", attachments = attachments)
        }
    }

    fun sendAttachment(chatID: String, attachment: AttachmentRequest) {
        viewModelScope.launch(handler) {
            socketManager.emit(
                MessageRequest,
                MessageRequest(
                    chatId = chatID,
                    text = "",
                    attachments = listOf(attachment),
                    type = ""
                )
            )
        }
    }

    fun modifyChat() {
        viewModelScope.launch(handler) {
            avcSdkChat.modifyChat(
                chatId = "75e14687-1e6a-4c9e-8d92-0fe342be6e7e",
                title = "Test 6 Chat 3p Android",
                participants = listOf(10186, 10128, 10137),
                maxParticipants = 2,
                allowAnonymous = true
            )
        }
    }

    fun createChat(
        title: String,
        participants: List<Int>,
        maxParticipants: Int,
        allowAnonymous: Boolean = true
    ) {
        Log.i("listParticipants", participants.toString())
        viewModelScope.launch(handler) {
            avcSdkChat.createChat(
                title = title,
                participants = participants,
                maxParticipants = maxParticipants,
                allowAnonymous = allowAnonymous
            )
        }
    }


    fun deleteChatHistory(chatID: String) {
        viewModelScope.launch(handler) {
            avcSdkChat.deleteChatHistory(chatId = chatID)
        }
    }


    fun getSdkUser() {
        viewModelScope.launch(handler) {
            avcSdkUser.getUser()
            userResponseLiveData.postValue(avcSdkUser.getUser())
            Log.i("avcSdkUser", user.toString())
        }
    }

    // attachment
    fun uploadFile(uri: Uri, chatId: String, uploadCallbacks: UploadCallbacks) {
        viewModelScope.launch(handler) {
            avcSdkAttachment.uploadFile(uri, chatId, uploadCallbacks)
        }
    }

    fun downloadFile(attachment: Attachment, downloadCallback: DownloadCallback) {
        viewModelScope.launch(handler) {
            avcSdkAttachment.downloadFile(attachment, downloadCallback)
        }
    }

    // CALL - VIDEO
    fun startCall(chatId: String, isVideo: Boolean) {
        viewModelScope.launch(handler) {
            callSdk.startCall(chatId, isVideo)
        }
    }

    fun acceptCall(chatId: String, isVideo: Boolean) {
        viewModelScope.launch(handler) {
            callSdk.acceptCall(chatId, isVideo, "")
        }
    }

    fun declineCall(chatId: String) {
        viewModelScope.launch(handler) {
            callSdk.declineCall(chatId)
        }
    }

    fun endCall() {
        viewModelScope.launch(handler) {
            //  callSdk.endCall("")
        }
    }

    private fun observeNetwork() {
        connectivityObserver.networkStatusFlow()
            .onStart {
                //   avcSdkManager.restartSocket()
                Log.i("observeNetwork", connectivityObserver.getStatus().toString())
                emit(connectivityObserver.getStatus())
            }
            .onEach {
                Log.i("observeNetwork", it.toString())
                _showInternetConnectionError.emit(it != Status.AVAILABLE)
            }
            .launchIn(viewModelScope)

        connectivityObserver.networkStatusFlow()
            .drop(1)
            .onEach {
                when (it) {
                    // Status.AVAILABLE -> avcSdkManager.restartSocket()
                    else -> {
                        Log.i("observeNetwork", "false")
                    }
                }
            }
            .launchIn(viewModelScope)
    }

}
