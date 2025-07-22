package com.example.zdravlje.data.repository.fitnes

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.zdravlje.data.model.fitnes.DailyFitnessModel
import com.example.zdravlje.data.model.fitnes.WeeklyFitnessModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


interface FitnessRepository {
    fun getDailyFitnessData(context: Context): MutableLiveData<DailyFitnessModel>
    fun getWeeklyFitnessData(context: Context): MutableLiveData<WeeklyFitnessModel>
    fun getGoogleAccount(context: Context): GoogleSignInAccount
}