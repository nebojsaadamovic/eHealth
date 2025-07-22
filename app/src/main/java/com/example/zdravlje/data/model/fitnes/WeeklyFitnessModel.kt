package com.example.zdravlje.data.model.fitnes

import com.example.zdravlje.data.model.fitnes.DailyFitnessModel

data class WeeklyFitnessModel(
    val dailyFitnessList: List<DailyFitnessModel>
)