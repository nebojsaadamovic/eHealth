package com.example.zdravlje.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository

class LoginViewModelFactory(
    private val loginRepository: LoginRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(loginRepository, dataStoreRepository) as T
    }
}