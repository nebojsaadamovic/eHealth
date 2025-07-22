package com.example.zdravlje.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zdravlje.data.model.request.dashobard.EventsAndActivitiesRequest
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.data.repository.DashboardRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.SingleLiveEvent
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

data class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var dateParem: MutableLiveData<String> = MutableLiveData()

    //Dashboard
    val dashboardFutureResponseLiveData: MutableLiveData<Response<List<DailyEvents>>> =
        MutableLiveData()
    val dashboardFutureResponseErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    val dashboardPastResponseLiveData: MutableLiveData<Response<List<DailyEvents>>> =
        MutableLiveData()
    val dashboardPastResponseErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    private val _errorLiveData = SingleLiveEvent<Boolean>()
    val errorLiveData: LiveData<Boolean> = _errorLiveData

    fun getFutureDashboardData(eventsAndActivitiesRequest: EventsAndActivitiesRequest) {
        viewModelScope.launch {
            val response = try {
                dashboardRepository.getFutureDashboardData(eventsAndActivitiesRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                dashboardFutureResponseLiveData.value = response
            } else {
                if (response.code()==401){
                    _errorLiveData.postValue(true)
                }
                dashboardFutureResponseErrorLiveData.value = response
            }
        }
    }

    fun getPastDashboardEvents(eventsAndActivitiesRequest: EventsAndActivitiesRequest) {
        viewModelScope.launch {
            val response = try {
                dashboardRepository.getPastDashboardEvents(eventsAndActivitiesRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }

            if (response.isSuccessful) {
                dashboardPastResponseLiveData.value = response
            } else {
                dashboardPastResponseErrorLiveData.value = response
            }
        }
    }
}

