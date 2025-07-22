package com.example.zdravlje.services.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.democall.composesample.util.observers.network.ConnectivityObserver
import com.app.democall.sdk.manager.api.AvcSdkManager

class MainViewModelFactory(
    private val avcSdkManager: AvcSdkManager,
    private val connectivityObserver: ConnectivityObserver

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(avcSdkManager, connectivityObserver) as T
    }
}