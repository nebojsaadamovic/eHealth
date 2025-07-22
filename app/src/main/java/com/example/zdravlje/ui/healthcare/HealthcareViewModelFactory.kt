package com.example.zdravlje.ui.healthcare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository

class HealthcareViewModelFactory(
    private val healthcareRepository: HealthcareRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HealthcareViewModel(healthcareRepository, dataStoreRepository) as T
    }
}