package com.example.zdravlje.services.avc

import android.app.Application
import android.content.Context
import com.app.democall.sdk.call.api.AvcSdkCall
import com.app.democall.sdk.manager.api.AvcSdkManager
import com.app.democall.socket.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SampleApplication : Application() {

    lateinit var avcSdkManager: AvcSdkManager
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        instance = this
        AvcSdkManagerProvider.init(this)
        avcSdkManager = AvcSdkManagerProvider.getManager().apply {
            prepare("https://vcapp.nites.rs", false)
        }
    }

    fun getAVC(): AvcSdkManager {
        return avcSdkManager
    }

    fun getSocketManager(): SocketManager {
        return avcSdkManager.container.socketManager
    }

    fun getCallManager(): AvcSdkCall {
        return avcSdkManager.container.callSdk
    }

    override fun onTerminate() {
        // Called when the application is terminated.
        stopAvcSdk()
        super.onTerminate()
    }

    fun stopAvcSdk() {
        applicationScope.launch {
            avcSdkManager.stop()
        }
    }

    companion object {
        var instance: SampleApplication? = null
            private set
        val context: Context?
            get() = instance
    }

}