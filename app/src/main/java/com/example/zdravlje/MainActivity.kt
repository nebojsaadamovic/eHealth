package com.example.zdravlje

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.zdravlje.data.model.fitnes.HealthDataEntry
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.splash.SplashActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.data.SleepStages
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    private val healthDataList = mutableListOf<HealthDataEntry>()


    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_OXYGEN_SATURATION, FitnessOptions.ACCESS_READ)

        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

     //   checkPermissionsAndRun()
        loadNavController()
    }

    private fun checkPermissionsAndRun() {
        if (permissionApproved()) {
            fitSignIn()
        } else {
            requestRuntimePermissions()
        }
    }

    private fun permissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    private fun requestRuntimePermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACTIVITY_RECOGNITION
        )

        if (shouldProvideRationale) {
            showPermissionSnackbar()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun showPermissionSnackbar() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "Permission Denied",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Settings") {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
            )
        }.show()
    }

    private fun fitSignIn() {
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            requestPermissions()
        } else {
            //  val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
            //  googleSignInClient.signOut()

            loadNavController()
            fetchFitnessData()
        }
    }

    private fun requestPermissions() {
        GoogleSignIn.requestPermissions(
            this, GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, getGoogleAccount(), fitnessOptions
        )
    }

    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    private fun fetchFitnessData() {
        readHeightAndWeight()
        readAdditionalHealthData()
        readBodyMassIndex()
        readAggregateData()
    }

    //Body mass index
    private fun readBodyMassIndex() {
        val (startTime, endTime) = getTimeRange()

        val weightRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_WEIGHT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        val heightRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_HEIGHT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        // Read weight data
        Fitness.getHistoryClient(this, getGoogleAccount())
            .readData(weightRequest)
            .addOnSuccessListener { weightResponse ->
                val weight = getLatestValue(weightResponse, "Weight")
                if (weight != null) {
                    // Read height data after getting weight
                    Fitness.getHistoryClient(this, getGoogleAccount())
                        .readData(heightRequest)
                        .addOnSuccessListener { heightResponse ->
                            val height = getLatestValue(heightResponse, "Height")
                            if (height != null) {
                                val bmi = calculateBMI(weight, height)
                                Log.i("Google Fit", "BMI: $bmi")
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Google Fit", "Error reading weight data", e)
            }
    }

    private fun getLatestValue(response: DataReadResponse, label: String): Float? {
        for (dataSet in response.dataSets) {
            for (dataPoint in dataSet.dataPoints) {
                for (field in dataPoint.dataType.fields) {
                    return dataPoint.getValue(field)?.asFloat() // Return the latest value
                }
            }
        }
        return null
    }

    private fun calculateBMI(weight: Float, height: Float): Float {
        // Height should be converted from cm to m
        return weight / (height * height) // Calculate BMI
    }
    //end Body mass index


    // Read aggregated data like steps, calories, distance, etc.
    private fun readAggregateData() {
        var healthDataEntries = mutableListOf<HealthDataEntry>()

        val (startTime, endTime) = getTimeRange()

        val dataReadRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
            .aggregate(DataType.TYPE_HEART_POINTS)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this, getGoogleAccount())
            .readData(dataReadRequest)
            .addOnCompleteListener { task: Task<DataReadResponse> ->
                if (task.isSuccessful) {
                    healthDataEntries = handleFitnessResponse(task.result)
                } else {
                    Log.w("Google Fit", "Problem getting aggregated data.", task.exception)
                }
            }
    }

    // Read height and weight separately
    private fun readHeightAndWeight() {
        val (startTime, endTime) = getTimeRange()

        val dataReadRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_HEIGHT)
            .read(DataType.TYPE_WEIGHT)
            .read(DataType.TYPE_SLEEP_SEGMENT)
            .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this, getGoogleAccount())
            .readData(dataReadRequest)
            .addOnCompleteListener { task: Task<DataReadResponse> ->
                if (task.isSuccessful) {
                    handleHeightWeightResponse(task.result)
                } else {
                    Log.w("Google Fit", "Problem getting height/weight data.", task.exception)
                }
            }
    }

    // Handle aggregated fitness data (steps, calories, etc.)
    private fun handleFitnessResponse(response: DataReadResponse): MutableList<HealthDataEntry> {
        // Kreiranje mape za čuvanje podataka po datumu
        val healthDataEntries = mutableListOf<HealthDataEntry>()

        val dataByDate = mutableMapOf<Long, MutableList<DataPoint>>()

        for (bucket in response.buckets) {
            // Svaki datum koji dobijete
            val date = bucket.getStartTime(TimeUnit.MILLISECONDS)

            // Prikupite sve DataPoint-ove iz ovog bucketa
            for (dataSet in bucket.dataSets) {
                for (dataPoint in dataSet.dataPoints) {
                    // Dodajte DataPoint u mapu za odgovarajući datum
                    if (dataByDate[date] == null) {
                        dataByDate[date] = mutableListOf()
                    }
                    dataByDate[date]?.add(dataPoint)
                }
            }
        }

        // Prolazite kroz mapu i logujte podatke po datumu
        for ((date, dataPoints) in dataByDate) {
            for (dataPoint in dataPoints) {
                for (field in dataPoint.dataType.fields) {
                    Log.i("Google Fit", "Date: $date, ${field.name}: ${dataPoint.getValue(field)}")
                    healthDataList.add(
                        HealthDataEntry(
                            date.toString(),
                            field.name,
                            dataPoint.getValue(field).toString()
                        )
                    )
                }
            }
        }

        Log.i("ttas1212", healthDataList.toString())

        return healthDataList
    }

    // Handle height and weight response
    private fun handleHeightWeightResponse(response: DataReadResponse) {
        for (dataSet in response.dataSets) {
            for (dataPoint in dataSet.dataPoints) {
                // Get the date from the DataPoint
                val date = dataPoint.getStartTime(TimeUnit.MILLISECONDS)

                for (field in dataPoint.dataType.fields) {
                    Log.i("field1", "${field.name}: ${dataPoint.getValue(field)}, Date: $date")
                }
            }

            if (dataSet.dataType == DataType.TYPE_HEIGHT) {
                logData(dataSet, "Height")
            }
            if (dataSet.dataType == DataType.TYPE_WEIGHT) {
                logData(dataSet, "Weight")
            }
            if (dataSet.dataType == DataType.TYPE_SLEEP_SEGMENT) {
                Log.i("sssas", "Entered sleep data handling")
                logSleepData(dataSet)
            }
        }
    }

    // Log dataset information
    private fun logData(dataSet: DataSet?, label: String) {
        dataSet?.let {
            for (dataPoint in it.dataPoints) {
                // Get the date from the DataPoint
                val date = dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                for (field in dataPoint.dataType.fields) {
                    Log.i("Google Fit", "Date: $date, ${field.name}: ${dataPoint.getValue(field)}")

                    healthDataList.add(
                        HealthDataEntry(
                            date.toString(),
                            field.name,
                            dataPoint.getValue(field).toString()
                        )
                    )

                }
            }
        }
    }

    // Handle sleep data logging
    private fun logSleepData(dataSet: DataSet) {
        Log.i(
            "Sleep Data",
            "Data Points Count: ${dataSet.dataPoints.size}"
        )  // Log number of data points
        for (dataPoint in dataSet.dataPoints) {
            Log.i("Sleep Data", "Data Point: ${dataPoint.toString()}")  // Log entire data point
            val date = dataPoint.getStartTime(TimeUnit.MILLISECONDS)
            for (field in dataPoint.dataType.fields) {
                val value = dataPoint.getValue(field)
                Log.i("Google Fit", "Sleep Stage: ${field.name}: $value")
                healthDataList.add(
                    HealthDataEntry(
                        date.toString(),
                        field.name,
                        value.toString()
                    )
                )

            }
        }
    }

    // Time range for queries (e.g., daily)
    private fun getTimeRange(): Pair<Long, Long> {
        val endTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 0) // Change this to a week ago
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val startTime = calendar.timeInMillis
        return Pair(startTime, endTime)
    }

    // Additional health data: Body temperature, blood pressure, etc.
    private fun readAdditionalHealthData() {
        readHealthData(HealthDataTypes.TYPE_BODY_TEMPERATURE, "Body Temperature")
        readHealthData(HealthDataTypes.TYPE_BLOOD_PRESSURE, "Blood Pressure")
        readHealthData(HealthDataTypes.TYPE_BLOOD_GLUCOSE, "Blood glucose")
        readHealthData(HealthDataTypes.TYPE_OXYGEN_SATURATION, "Oxygen Saturation")
        // Add more types as needed
    }

    // Read specific health data
    private fun readHealthData(dataType: DataType, label: String) {
        val (startTime, endTime) = getTimeRange()

        val dataReadRequest = DataReadRequest.Builder()
            .read(dataType)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        Fitness.getHistoryClient(this, getGoogleAccount())
            .readData(dataReadRequest)
            .addOnCompleteListener { task: Task<DataReadResponse> ->
                if (task.isSuccessful) {
                    handleHealthDataResponse(task.result, label)
                } else {
                    Log.w("Google Fit", "Problem getting $label data.", task.exception)
                }
            }
    }

    // Handle response for additional health data
    private fun handleHealthDataResponse(response: DataReadResponse, label: String) {
        for (dataSet in response.dataSets) {
            for (dataPoint in dataSet.dataPoints) {
                // Get the date from the DataPoint
                val date = dataPoint.getStartTime(TimeUnit.MILLISECONDS)

                if (label == "Blood Pressure") {
                    // Get systolic and diastolic values
                    val systolic = dataPoint.getValue(dataPoint.dataType.fields[0]).asFloat()
                    val diastolic = dataPoint.getValue(dataPoint.dataType.fields[1]).asFloat()
                    val f1 = dataPoint.dataType.fields[0].name
                    val f2 = dataPoint.dataType.fields[1].name

                    healthDataList.add(
                        HealthDataEntry(
                            date.toString(),
                            f1,
                            systolic.toString()

                        )
                    )
                    healthDataList.add(
                        HealthDataEntry(
                            date.toString(),
                            f2,
                            diastolic.toString()
                        )
                    )


                    Log.i("Google Fit", "Date: $date, $f1: $systolic, $f2: $diastolic")
                } else {
                    val f1 = dataPoint.dataType.fields[0].name
                    Log.i(
                        "Google Fit",
                        "Date: $date, $f1: ${dataPoint.getValue(dataPoint.dataType.fields[0])}"
                    )

                    healthDataList.add(
                        HealthDataEntry(
                            date.toString(),

                            f1,
                            dataPoint.getValue(dataPoint.dataType.fields[0]).toString()
                        )
                    )

                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
//            loadNavController()
//            fetchFitnessData()
//        } else {
//            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
//            requestPermissions()
        }
            loadNavController()
            fetchFitnessData()
    }

    private fun loadNavController() {
        startActivity(Intent(this@MainActivity, SplashActivity::class.java))
        finish()

    }


}