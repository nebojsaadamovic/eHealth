package com.example.zdravlje.data.repository

import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.dashobard.EventsAndActivitiesRequest
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import retrofit2.Response

class DashboardRepository {

    suspend fun getFutureDashboardData(eventsAndActivitiesRequest: EventsAndActivitiesRequest): Response<List<DailyEvents>> {
        return RetrofitInstance.appApi.getFutureDashboardEvents(eventsAndActivitiesRequest)
    }

    suspend fun getPastDashboardEvents(eventsAndActivitiesRequest: EventsAndActivitiesRequest): Response<List<DailyEvents>> {
        return RetrofitInstance.appApi.getPastDashboardEvents(eventsAndActivitiesRequest)
    }
}