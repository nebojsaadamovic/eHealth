package com.example.zdravlje.ui.calendar.nextOnSchedule

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.dashobard.EventsAndActivitiesRequest
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.data.repository.DashboardRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.home.DashboardViewModel
import com.example.zdravlje.ui.home.DashboardViewModelFactory
import com.google.gson.Gson
import com.nites.ezdravljeapp.databinding.ActivityNextOnScheduleBinding

class NextOnScheduleActivity : BaseActivity() {

    lateinit var binding: ActivityNextOnScheduleBinding
    private val TAG = NextOnScheduleActivity::class.java.simpleName

    private var dailyEvents: DailyEvents? = null
    private val gson = Gson()
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    private val _dailyEventsLiveData = MutableLiveData<DailyEvents>()
    val dailyEventsLiveData: LiveData<DailyEvents> get() = _dailyEventsLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNextOnScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModels()

        val json = intent.getStringExtra("time_update_value")

        if (json != null) {
            val eventsAndActivitiesRequest = EventsAndActivitiesRequest(
                json + " 00:00",
                json + " 23:59",
                sort_in = "ASC",
                limit_in = 100000,
                mobile_group = 1
            )

            dashboardViewModel.getPastDashboardEvents(eventsAndActivitiesRequest)

        } else {
            val jsonObjectString = intent.getStringExtra("jsonObject")

            if (jsonObjectString != null) {
                dailyEvents = gson.fromJson(jsonObjectString, DailyEvents::class.java)
                _dailyEventsLiveData.value = dailyEvents!!
            }
        }

        subscribeToObservers()
    }

    private fun setViewModels() {
        val dashboardRepository = DashboardRepository()
        dataStoreRepository = DataStoreRepository(this)
        val dashboardViewModelFactory = DashboardViewModelFactory(dashboardRepository, dataStoreRepository)

        dashboardViewModel = ViewModelProvider(
            this,
            dashboardViewModelFactory
        )[DashboardViewModel::class.java]
    }

    private fun subscribeToObservers() {
        dashboardViewModel.dashboardPastResponseLiveData.observe(this) { response ->
            if (response.isSuccessful) {
                val body = response.body()
                if (!body.isNullOrEmpty()) {
                    dailyEvents = body.first()
                    _dailyEventsLiveData.value = dailyEvents!!
                } else {
                    Log.e(TAG, "Prazan response body")
                }
            } else {
                Log.e(TAG, "Neuspešan response: ${response.code()}")
            }
        }
    }

    fun getDailyEvents(): DailyEvents? = dailyEvents
}
