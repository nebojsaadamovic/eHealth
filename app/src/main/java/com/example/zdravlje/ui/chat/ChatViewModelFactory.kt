package com.example.zdravlje.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.ChatRepository
import com.example.zdravlje.data.repository.DataStoreRepository

class ChatViewModelFactory (
    private val chatRepository: ChatRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(chatRepository, dataStoreRepository) as T
    }
}