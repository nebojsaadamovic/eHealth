package com.example.zdravlje.ui.calendar

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.calendar.AddEvent
import com.example.zdravlje.data.model.request.calendar.DeleteEvent
import com.example.zdravlje.data.model.request.calendar.FamilyTimeSlots
import com.example.zdravlje.data.model.request.calendar.FamilyTimeSlotsForTelemedicine
import com.example.zdravlje.data.model.request.calendar.MedicineRequest
import com.example.zdravlje.data.model.request.calendar.RealizeEventRequest
import com.example.zdravlje.data.model.request.calendar.ScreeningProgramsRequest
import com.example.zdravlje.data.model.request.chat.EventDetailsRequest
import com.example.zdravlje.data.model.request.dashobard.EventsAndActivitiesRequest
import com.example.zdravlje.data.model.request.healthcare.RecipeRequest
import com.example.zdravlje.data.model.response.calendar.FamilyDoctor
import com.example.zdravlje.data.model.response.calendar.FamilyTimeSlotsResponse
import com.example.zdravlje.data.model.response.calendar.MedicineResponse
import com.example.zdravlje.data.model.response.calendar.ScheduleResponse
import com.example.zdravlje.data.model.response.calendar.TimeslotRequest
import com.example.zdravlje.data.model.response.calendar.TimeslotResponse
import com.example.zdravlje.data.model.response.chat.GenerateCallResponse
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.avc.SampleApplication.Companion.context
import com.example.zdravlje.ui.base.SingleLiveEvent
import com.nites.ezdravljeapp.R
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var selectedParticipants: List<com.example.zdravlje.data.model.response.chat.Participant> = emptyList()

    //calendar
    val getCalendarEventsForMonthResponseLiveData: MutableLiveData<Response<List<DailyEvents>>> =
        MutableLiveData()
    val getCalendarEventsForMonthErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    val getFamilyDoctorLiveData1: MutableLiveData<Response<List<FamilyDoctor>>> = MutableLiveData()
    val getFamilyDoctorErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    private val _getFamilyDoctorLiveData = SingleLiveEvent<Response<List<FamilyDoctor>>>()
    val getFamilyDoctorLiveData: LiveData<Response<List<FamilyDoctor>>> = _getFamilyDoctorLiveData

    private val _getFamilyTimeSlotsLiveData = SingleLiveEvent<Response<List<ScheduleResponse>>>()
    val getFamilyTimeSlotsLiveData: LiveData<Response<List<ScheduleResponse>>> = _getFamilyTimeSlotsLiveData

    val getFamilyTimeSlotsErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    val getFamilyTimeSlotsForTelemedicineLiveData: MutableLiveData<Response<List<FamilyTimeSlotsResponse>>> =
        MutableLiveData()
    val getFamilyTimeSlotsErrorForTelemedicineLiveData: MutableLiveData<Any> = MutableLiveData()

    val getMedicineLiveData: MutableLiveData<Response<List<MedicineResponse>>> = MutableLiveData()
    val getMedicineErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    private val _getMedicineSearchLiveData = SingleLiveEvent<Response<List<MedicineResponse>>>()
    val getMedicineSearchLiveData: LiveData<Response<List<MedicineResponse>>> = _getMedicineSearchLiveData

    val getMedicineErrorSearchLiveData: MutableLiveData<Any> = MutableLiveData()

    private val _realizeEventLiveData = SingleLiveEvent<Response<Any>>()
    val realizeEventLiveData: LiveData<Response<Any>> = _realizeEventLiveData

    private val _createEventAnNotificationLiveData = SingleLiveEvent<Response<Any>>()
    val createEventAnNotificationLiveData: LiveData<Response<Any>> =
        _createEventAnNotificationLiveData
    val getCreateEventAnNotificationErrorLiveData: MutableLiveData<Response<Any>> =
        MutableLiveData()

    val getDeleteEventLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    val getDeleteEventErrorLiveData: MutableLiveData<String> = MutableLiveData()

    var getScheduleAppointmentLiveData: MutableLiveData<Response<TimeslotResponse>> =
        MutableLiveData()
    var getScheduleAppointmentErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getUpdateEventLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    val getUpdateEventErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val getCreateCallLiveData: MutableLiveData<Response<GenerateCallResponse>> = MutableLiveData()
    val getCreateCallErrorLiveData: MutableLiveData<Response<GenerateCallResponse>> = MutableLiveData()

    val getUpdateCallLiveData: MutableLiveData<Response<GenerateCallResponse>> = MutableLiveData()
    val getUpdateCallErrorLiveData: MutableLiveData<Response<GenerateCallResponse>> = MutableLiveData()


    val getUpdateEventNotificationLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    val getUpdateEventNotificationErrorLiveData: MutableLiveData<Response<Any>> = MutableLiveData()

    val getAvailableScreeningProgramsLiveData: MutableLiveData<Response<ScreeningProgramsRequest>> =
        MutableLiveData()
    val getAvailableScreeningProgramsErrorLiveData: MutableLiveData<Response<ScreeningProgramsRequest>> =
        MutableLiveData()

    val getPatienteMedicamenteLiveData: MutableLiveData<Response<List<PrescribedListMedicamentResponse>>> = MutableLiveData()
    val getPatienteMedicamenteErrorLiveData: MutableLiveData<Response<List<PrescribedListMedicamentResponse>>> =
        MutableLiveData()

    fun getCalendarEventsForMonth(eventsAndActivitiesRequest: EventsAndActivitiesRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.getCalendarEventsForMonth(eventsAndActivitiesRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                getCalendarEventsForMonthResponseLiveData.value = response
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")
                getCalendarEventsForMonthErrorLiveData.value = errorMessage
            }
        }
    }

    fun getFamilyDoctors(pinRequest: PinRequest) {
        viewModelScope.launch {
            try {
                val response = calendarRepository.getFamilyDoctors(pinRequest)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body is List<*>) {
                        _getFamilyDoctorLiveData.postValue(Response.success(body))
                    } else {
                        _getFamilyDoctorLiveData.postValue(Response.success(emptyList()))
                    }
                } else {
                    getFamilyDoctorErrorLiveData.value =
                        response.errorBody()?.string() ?: "Unknown error"
                }
            } catch (e: Exception) {
                getFamilyDoctorErrorLiveData.value = e.message ?: "Network error"
            }
        }
    }


    fun getFamilyTimeSlots(familyTimeSlots: FamilyTimeSlots) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.getFamilyTimeSlots(familyTimeSlots)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                _getFamilyTimeSlotsLiveData.postValue(response)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")
                getFamilyTimeSlotsErrorLiveData.value = errorMessage
            }
        }
    }

    fun scheduleAppointment(timeslotRequest: TimeslotRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.scheduleAppointment(timeslotRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                getScheduleAppointmentLiveData.postValue(response)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")
                getScheduleAppointmentErrorLiveData.value = errorMessage
            }
        }
    }


    fun getFamilyTimeSlotsForTelemedicine(familyTimeSlotsForTelemedicine: FamilyTimeSlotsForTelemedicine) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.getFamilyTimeSlotsForTelemedicine(familyTimeSlotsForTelemedicine)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                getFamilyTimeSlotsForTelemedicineLiveData.value = response
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")
                getFamilyTimeSlotsErrorForTelemedicineLiveData.value = errorMessage
            }
        }
    }

    fun getMedicament(medicineRequest: MedicineRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.getMedicament(medicineRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                getMedicineLiveData.value = response
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")
                getMedicineErrorLiveData.value = errorMessage
            }
        }
    }

    fun getMedicamentSearch(medicineRequest: MedicineRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.getMedicament(medicineRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                _getMedicineSearchLiveData.postValue(response)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")
                getMedicineErrorSearchLiveData.value = errorMessage
            }
        }
    }

    fun realizeEvent(realizeEventRequest: RealizeEventRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.realizeEvent(realizeEventRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            _realizeEventLiveData.postValue(response)
        }
    }

    fun createEventAnNotification(addEvent: AddEvent) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.createEventAndNotification(addEvent)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                _createEventAnNotificationLiveData.postValue(response)
            } else {
                getCreateEventAnNotificationErrorLiveData.value = response
            }
        }
    }

    fun updateEventAndNotification(addEvent: AddEvent) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.updateEventAndNotification(addEvent)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                getUpdateEventNotificationLiveData.value = response
            } else {
                getUpdateEventNotificationErrorLiveData.value = response
            }
        }
    }

    fun deleteEvent(deleteEvent: DeleteEvent) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.deleteEvent(deleteEvent)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                getDeleteEventLiveData.value = response
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")
                getDeleteEventErrorLiveData.value = errorMessage
            }
        }
    }

    fun updateEvent(updateEvent: Event) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.updateEvent(updateEvent)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                getUpdateEventLiveData.value = response
            } else {
                if (response.code() == 500) {
                    Log.d("Nesupjesno", "Server error (500): ${response.message()}")
                } else {
                    Log.d(
                        "Nesupjesno",
                        "Error code: ${response.code()}, Message: ${response.message()}"
                    )
                }
                val errorBody = response.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                val errorMessage = errorJson.getString("message")
                getUpdateEventErrorLiveData.value = errorMessage
            }
        }
    }

    fun createCall(eventDetailsRequest: EventDetailsRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.createCall(eventDetailsRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                getCreateCallLiveData.value = response
            } else {
                getCreateCallErrorLiveData.value = response
            }
        }
    }

    fun updateCall(eventDetailsRequest: EventDetailsRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.updateCall(eventDetailsRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                getUpdateCallLiveData.value = response
            } else {
                getUpdateCallErrorLiveData.value = response
            }
        }
    }

    fun availableScreeningPrograms(pinRequest: PinRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.availableScreeningPrograms(pinRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                getAvailableScreeningProgramsLiveData.value = response
            } else {
                getAvailableScreeningProgramsErrorLiveData.value = response
            }
        }
    }

    fun searchPatientMedicine(recipeRequest: RecipeRequest) {
        viewModelScope.launch {
            val response = try {
                calendarRepository.searchPatientMedicine(recipeRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                getPatienteMedicamenteLiveData.value = response
            } else {
                getPatienteMedicamenteErrorLiveData.value = response
            }
        }
    }

}
