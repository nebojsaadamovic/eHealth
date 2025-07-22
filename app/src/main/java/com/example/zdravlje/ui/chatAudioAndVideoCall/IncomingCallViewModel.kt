package com.example.zdravlje.ui.chatAudioAndVideoCall


import android.content.ContentValues
import android.content.Context
import android.provider.CallLog
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.democall.composesample.data.call.CallResult
import com.app.democall.composesample.data.call.LeftCall
import com.app.democall.composesample.util.call.CallManager
import com.app.democall.composesample.util.observers.network.ConnectivityObserver
import com.app.democall.socket.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class IncomingCallViewModel(
    private val chatId: String,
    private val isVideo: Boolean,
    private val socketManager: SocketManager,
    private val callManager: CallManager,
    private val screenStateManager: com.example.zdravlje.ui.chatAudioAndVideoCall.ScreenStateManager,
    //private val chatManager: ChatManager,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    val viewModelOutliveScope = CoroutineScope(SupervisorJob())

    private val _finish = MutableStateFlow(false)
    val finish = _finish.asStateFlow()

    private val _showInternetConnectionError = MutableStateFlow(false)
    val showInternetConnectionError = _showInternetConnectionError.asStateFlow()

    fun hideInternetError() = viewModelScope.launch {
        _showInternetConnectionError.emit(false)
    }

    init {
        observeInternetConnection()
        listenIsCallIsAlive()
    }

    private fun observeInternetConnection() {
        connectivityObserver.networkStatusFlow()
            .onStart {
                emit(connectivityObserver.getStatus())
            }
            .map {
                it != ConnectivityObserver.Status.AVAILABLE
            }
            .onEach {
                _showInternetConnectionError.emit(it)
            }
            .launchIn(viewModelScope)
    }

    private fun listenIsCallIsAlive() {
        socketManager.listenEvent(
            LeftCall,
            CallResult::class.java
        ) {
            viewModelScope.launch {
                _finish.emit(true)
            }
        }
    }

    fun startChat() {
        // chatManager.startChat(chatId)
    }

    fun onDeclineClicked() {
        viewModelScope.launch {
            callManager.decline(chatId)
            _finish.emit(true)
        }
    }

    fun onAccept() {
        viewModelScope.launch {
           // callManager.accept(chatId, isVideo,"")
            _finish.emit(true)
        }
    }

    fun updateCurrentScreenState() {
        viewModelScope.launch {
            screenStateManager.pushState(
                IncomingCall(
                    false
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelOutliveScope.launch {
            if (!_finish.value) {
                callManager.decline(chatId)
            }
        }
    }

    fun addCallLog(context: Context, number: String, type: Int) {
        try {
            val values = ContentValues().apply {
                put(CallLog.Calls.NUMBER, number)
                put(CallLog.Calls.TYPE, type)
                put(CallLog.Calls.DATE, System.currentTimeMillis())
                put(CallLog.Calls.NEW, 1)
                put(CallLog.Calls.CACHED_NAME, "")
                put(CallLog.Calls.CACHED_NUMBER_TYPE, 0)
                put(CallLog.Calls.CACHED_NUMBER_LABEL, "")
            }
            context.contentResolver.insert(CallLog.Calls.CONTENT_URI, values)
            Log.i("PRPRPR", "addCallLog -> Success $values")
        } catch (e: SecurityException) {
            Log.i("PRPRPR", "addCallLog -> SecurityException $e")
        }
    }
}
