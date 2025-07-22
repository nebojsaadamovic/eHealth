package com.example.zdravlje.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DashboardRepository
import com.example.zdravlje.data.repository.DataStoreRepository

class DashboardViewModelFactory(
    private val dashboardRepository: DashboardRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(dashboardRepository, dataStoreRepository) as T
    }
}