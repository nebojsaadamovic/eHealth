package com.example.zdravlje.data.repository.fitnes

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.zdravlje.data.model.fitnes.DailyFitnessModel
import com.example.zdravlje.data.model.fitnes.WeeklyFitnessModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import java.util.*
import java.util.concurrent.TimeUnit

class FitnessRepositoryImpl(): FitnessRepository {

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)

        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_HEIGHT_SUMMARY, FitnessOptions.ACCESS_READ)

        .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY, FitnessOptions.ACCESS_READ)



        .build()

    override fun getDailyFitnessData(context: Context): MutableLiveData<DailyFitnessModel> {
        val dailyFitnessLiveData = MutableLiveData<DailyFitnessModel>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
            .aggregate(DataType.TYPE_WEIGHT, DataType.AGGREGATE_WEIGHT_SUMMARY)
            .aggregate(DataType.TYPE_HEIGHT, DataType.AGGREGATE_HEIGHT_SUMMARY)
            .aggregate(DataType.TYPE_BODY_FAT_PERCENTAGE, DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY)

            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, getGoogleAccount(context))
            .readData(readRequest)
            .addOnSuccessListener { data ->
                Log.i("Tdds",data.buckets.toString())
                val buckets = data.buckets
                val bucket = if (buckets.isNotEmpty()) buckets[0] else null
                var stepCount = 0
                var calories = 0
                var distance = 0.0f
                var height = 0.0f
                var weight = 0.0f
                bucket?.dataSets?.forEach { dataSet ->
                    dataSet.dataPoints.forEach { dataPoint ->
                        Log.i("sdasd", dataPoint.originalDataSource.toString())
                        Log.i("sdasd", dataPoint.dataSource.toString())
                        when (dataPoint.dataType) {
                            DataType.TYPE_STEP_COUNT_DELTA -> {
                                Log.i("ttre","TYPE_STEP_COUNT_DELTA")
                                Log.i("ttre", dataPoint.getValue(Field.FIELD_STEPS).toString())
                                stepCount = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                            }
                            DataType.TYPE_CALORIES_EXPENDED -> {
                                Log.i("ttre","TYPE_CALORIES_EXPENDED")
                                Log.i("ttre", dataPoint.getValue(Field.FIELD_CALORIES).toString())

                                calories = dataPoint.getValue(Field.FIELD_CALORIES).asFloat().toInt()
                            }
                            DataType.TYPE_DISTANCE_DELTA -> {
                                Log.i("ttre","TYPE_DISTANCE_DELTA")
                                Log.i("ttre", dataPoint.getValue(Field.FIELD_DISTANCE).toString())

                                distance = dataPoint.getValue(Field.FIELD_DISTANCE).asFloat() / 1000
                            }
                            DataType.AGGREGATE_WEIGHT_SUMMARY -> {

                                Log.i("ttre", dataPoint.getValue(Field.FIELD_AVERAGE).toString())

                                weight = dataPoint.getValue(Field.FIELD_AVERAGE).asFloat() // Ovo pretpostavlja da je težina izražena u kilogramima
                            }
                            DataType.AGGREGATE_HEIGHT_SUMMARY->{
                                Log.i("ttre", dataPoint.getValue(Field.FIELD_AVERAGE).toString())
                                height = dataPoint.getValue(Field.FIELD_AVERAGE).asFloat() // Ovo pretpostavlja da je težina izražena u kilogramima
                            }
//                            DataType.AGGREGATE_BODY_FAT_PERCENTAGE_SUMMARY->{
//                                Log.i("ttrePerce", dataPoint.getValue(Field.FIELD_AVERAGE).toString())
//                            }
//                            DataType.TYPE_BODY_FAT_PERCENTAGE->{
//                                Log.i("ttrePerce", dataPoint.getValue(Field.FIELD_AVERAGE).toString())
//                            }
                        }
                    }
                }
                val dailyFitness = DailyFitnessModel(stepCount, calories, distance, height,weight)
                dailyFitnessLiveData.postValue(dailyFitness)
            }
            .addOnFailureListener { exception ->
                Log.i("ttre", exception.localizedMessage)
            }

        return dailyFitnessLiveData
    }

    override fun getWeeklyFitnessData(context: Context): MutableLiveData<WeeklyFitnessModel> {
        val weeklyFitnessLiveData = MutableLiveData<WeeklyFitnessModel>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startTime = calendar.timeInMillis

        // Build data read request for the last 7 days
        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)

            .aggregate(DataType.TYPE_WEIGHT, DataType.AGGREGATE_WEIGHT_SUMMARY)

            .aggregate(DataType.TYPE_HEIGHT, DataType.AGGREGATE_HEIGHT_SUMMARY)

            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(context, getGoogleAccount(context))
            .readData(readRequest)
            .addOnSuccessListener { data ->
                val buckets = data.buckets
                val dailyFitnessList = mutableListOf<DailyFitnessModel>()

                // Process each bucket of fitness data
                buckets.forEach { bucket ->
                    var stepCount = 0
                    var calories = 0
                    var distance = 0.0f
                    var height = 0.0f

                    bucket.dataSets.forEach { dataSet ->
                        dataSet.dataPoints.forEach { dataPoint ->
                            when (dataPoint.dataType) {
                                DataType.TYPE_STEP_COUNT_DELTA -> {
                                    stepCount = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                                }
                                DataType.TYPE_CALORIES_EXPENDED -> {
                                    calories = dataPoint.getValue(Field.FIELD_CALORIES).asFloat().toInt()
                                }
                                DataType.TYPE_DISTANCE_DELTA -> {
                                    distance = dataPoint.getValue(Field.FIELD_DISTANCE).asFloat() / 1000
                                }
                                DataType.TYPE_WEIGHT -> {
                                    Log.i("ttre", dataPoint.getValue(Field.FIELD_WEIGHT).toString())

                                    height = dataPoint.getValue(Field.FIELD_WEIGHT).asFloat() // Ovo pretpostavlja da je težina izražena u kilogramima
                                }
                                DataType.TYPE_HEIGHT->{
                                    Log.i("ttre", dataPoint.getValue(Field.FIELD_WEIGHT).toString())
                                    height = dataPoint.getValue(Field.FIELD_WEIGHT).asFloat() // Ovo pretpostavlja da je težina izražena u kilogramima

                                }

                            }
                        }
                    }

                    val dailyFitness = DailyFitnessModel(stepCount, calories, distance,height,0f)
                    dailyFitnessList.add(dailyFitness)
                }

                val weeklyFitness = WeeklyFitnessModel(dailyFitnessList)
                weeklyFitnessLiveData.postValue(weeklyFitness)
            }
            .addOnFailureListener { exception ->
                // Handle error
            }

        return weeklyFitnessLiveData
    }

    override fun getGoogleAccount(context: Context): GoogleSignInAccount = GoogleSignIn.getAccountForExtension(context, fitnessOptions)


}