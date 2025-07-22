package com.example.zdravlje.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository

class CalendarViewModelFactory (
    private val calendarRepository: CalendarRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalendarViewModel(calendarRepository, dataStoreRepository) as T
    }
}