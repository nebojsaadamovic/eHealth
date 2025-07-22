package com.example.zdravlje.ui.home.dashboard

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zdravlje.data.model.fitnes.DailyFitnessModel
import com.example.zdravlje.data.model.fitnes.WeeklyFitnessModel
import com.example.zdravlje.data.repository.fitnes.FitnessRepository
import com.example.zdravlje.data.repository.fitnes.FitnessRepositoryImpl
import com.example.zdravlje.data.repository.fitnes.SharedPreferencesRepository
import com.example.zdravlje.data.repository.fitnes.SharedPreferencesRepositoryImpl

class FitnessViewModel : ViewModel() {

    val fitnessRepo: FitnessRepository = FitnessRepositoryImpl()
    val sharedPreferencesRepo: SharedPreferencesRepository = SharedPreferencesRepositoryImpl()

    fun getDailyFitnessData(context: Context): LiveData<DailyFitnessModel> {
        var dailyFitnessLiveData = fitnessRepo.getDailyFitnessData(context)
        return dailyFitnessLiveData
    }

    fun getWeeklyFitnessData(context: Context): LiveData<WeeklyFitnessModel> {
        var weeklyFitnessLiveData = fitnessRepo.getWeeklyFitnessData(context)
        return weeklyFitnessLiveData
    }

    fun saveObjectiveSteps(context: Context, objectiveSteps: Int) {
        sharedPreferencesRepo.saveObjectiveSteps(context, objectiveSteps)
    }

    fun loadObjectiveSteps(context: Context): Int {
        return sharedPreferencesRepo.loadObjectiveSteps(context)
    }
}