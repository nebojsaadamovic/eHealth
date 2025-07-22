package com.example.zdravlje.services.avc

import android.app.Application
import com.app.democall.sdk.manager.api.AvcSdkManager
import com.app.democall.sdk.manager.impl.AvcSdkManagerImpl

object AvcSdkManagerProvider {

    private lateinit var manager: AvcSdkManager

    fun init(application: Application) {
        manager = AvcSdkManagerImpl(application)
    }

    fun getManager(): AvcSdkManager = manager
}
