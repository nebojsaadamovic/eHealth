package com.example.zdravlje.data.repository

import com.example.zdravlje.data.api.RetrofitInstance
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
import retrofit2.Response

class CalendarRepository {

    suspend fun getCalendarEventsForMonth(eventsAndActivitiesRequest: EventsAndActivitiesRequest): Response<List<DailyEvents>> {
        return RetrofitInstance.appApi.getCalendarEventsForMonth(eventsAndActivitiesRequest)
    }

    suspend fun getFamilyDoctors(pinRequest: PinRequest): Response<List<FamilyDoctor>> {
        return RetrofitInstance.appApi.getFamilyDoctors(pinRequest)
    }

    suspend fun getFamilyTimeSlots(familyTimeSlots: FamilyTimeSlots): Response<List<ScheduleResponse>> {
        return RetrofitInstance.appApi.getFamilyTimeSlots(familyTimeSlots)
    }

    suspend fun scheduleAppointment(timeslotRequest: TimeslotRequest): Response<TimeslotResponse> {
        return RetrofitInstance.appApi.scheduleAppointment(timeslotRequest)
    }

    suspend fun getFamilyTimeSlotsForTelemedicine(familyTimeSlotsForTelemedicine: FamilyTimeSlotsForTelemedicine): Response<List<FamilyTimeSlotsResponse>> {
        return RetrofitInstance.appApi.getFamilyTimeSlotsForTelemedicine(
            familyTimeSlotsForTelemedicine
        )
    }

    suspend fun getMedicament(medicineRequest: MedicineRequest): Response<List<MedicineResponse>> {
        return RetrofitInstance.appApi.getMedicament(medicineRequest)
    }

    suspend fun realizeEvent(realizeEventRequest: RealizeEventRequest): Response<Any> {
        return RetrofitInstance.appApi.realizeEvent(realizeEventRequest)
    }

    suspend fun createEventAndNotification(addEvent: AddEvent): Response<Any> {
        return RetrofitInstance.appApi.createEventAndNotification(addEvent)
    }

    suspend fun updateEventAndNotification(addEvent: AddEvent): Response<Any> {
        return RetrofitInstance.appApi.updateEventAndNotification(addEvent)
    }

    suspend fun deleteEvent(deleteEvent: DeleteEvent): Response<Any> {
        return RetrofitInstance.appApi.deleteEvent(deleteEvent)
    }

    suspend fun updateEvent(updateEvent: Event): Response<Any> {
        return RetrofitInstance.appApi.updateEvent(updateEvent)
    }

    suspend fun createCall(eventDetailsRequest: EventDetailsRequest): Response<GenerateCallResponse> {
        return RetrofitInstance.appApi.createCall(eventDetailsRequest)
    }

    suspend fun updateCall(eventDetailsRequest: EventDetailsRequest): Response<GenerateCallResponse> {
        return RetrofitInstance.appApi.updateCall(eventDetailsRequest)
    }

    suspend fun availableScreeningPrograms(pinRequest: PinRequest): Response<ScreeningProgramsRequest> {
        return RetrofitInstance.appApi.availableScreeningPrograms(pinRequest)
    }

    suspend fun searchPatientMedicine(recipeRequest: RecipeRequest): Response<List<PrescribedListMedicamentResponse>> {
        return RetrofitInstance.appApi.searchPatientMedicine(recipeRequest)
    }
}