package com.example.zdravlje.ui.chatAudioAndVideoCall

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.democall.socket.SocketManager
import com.app.democall.composesample.util.call.CallManager
import com.app.democall.composesample.util.observers.network.ConnectivityObserver
import com.app.democall.sdk.call.api.AvcSdkCall

class CallViewModelFactory(
    private val url: String,
    private val token: String,
    private val isVideo: Boolean,
    private val chatId: String,
    private val socketManager: SocketManager,
    private val callManager: AvcSdkCall,
    private val screenStateManager: ScreenStateManager,
    private val connectivityObserver: ConnectivityObserver,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CallViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CallViewModel(
                url,
                token,
                isVideo,
                chatId,
                socketManager,
                callManager,
                screenStateManager,
                connectivityObserver,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
