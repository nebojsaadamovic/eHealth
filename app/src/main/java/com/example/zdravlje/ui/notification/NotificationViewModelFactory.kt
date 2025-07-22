package com.example.zdravlje.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.notification.NotificationRepository

class NotificationViewModelFactory(
    private val notificationRepository: NotificationRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationViewModel(notificationRepository, dataStoreRepository) as T
    }
}
