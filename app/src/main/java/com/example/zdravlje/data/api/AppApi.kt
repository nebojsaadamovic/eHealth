package com.example.zdravlje.data.api

import com.example.zdravlje.data.model.request.AnonimusLoginRequest
import com.example.zdravlje.data.model.request.EidTokenRequest
import com.example.zdravlje.data.model.request.FirebaseModel
import com.example.zdravlje.data.model.request.LoginRequest
import com.example.zdravlje.data.model.request.LoginResponse
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.RfzoAuthRequest
import com.example.zdravlje.data.model.request.UpdateMyProfileRequest
import com.example.zdravlje.data.model.request.UserDeviceInfo
import com.example.zdravlje.data.model.request.calendar.AddEvent
import com.example.zdravlje.data.model.request.calendar.DeleteEvent
import com.example.zdravlje.data.model.request.calendar.ExamDeleteRequest
import com.example.zdravlje.data.model.request.calendar.FamilyTimeSlots
import com.example.zdravlje.data.model.request.calendar.FamilyTimeSlotsForTelemedicine
import com.example.zdravlje.data.model.request.calendar.MedicineRequest
import com.example.zdravlje.data.model.request.calendar.RealizeEventRequest
import com.example.zdravlje.data.model.request.calendar.ScreeningProgramsRequest
import com.example.zdravlje.data.model.request.chat.AcceptContactRequest
import com.example.zdravlje.data.model.request.chat.EventDetailsRequest
import com.example.zdravlje.data.model.request.chat.RemoveContactRequest
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest
import com.example.zdravlje.data.model.request.chat.SendContactListRequest
import com.example.zdravlje.data.model.request.chat.SendContactRequest
import com.example.zdravlje.data.model.request.dashobard.EventsAndActivitiesRequest
import com.example.zdravlje.data.model.request.healthcare.AddMedicamentRequest
import com.example.zdravlje.data.model.request.healthcare.CommonsThemesRequest
import com.example.zdravlje.data.model.request.healthcare.DeleteMedicamenteRequest
import com.example.zdravlje.data.model.request.healthcare.ESickRequest
import com.example.zdravlje.data.model.request.healthcare.ESickRequestDTO
import com.example.zdravlje.data.model.request.healthcare.EventHistoryDataRequest
import com.example.zdravlje.data.model.request.healthcare.FeedbackRequest
import com.example.zdravlje.data.model.request.healthcare.MKB10Response
import com.example.zdravlje.data.model.request.healthcare.MKB10SubResponse
import com.example.zdravlje.data.model.request.healthcare.MKB10SubResponseGroups
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonPdfRequest
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonRequest
import com.example.zdravlje.data.model.request.healthcare.PatmedDataRequest
import com.example.zdravlje.data.model.request.healthcare.PreventionDTO
import com.example.zdravlje.data.model.request.healthcare.RecipeRequest
import com.example.zdravlje.data.model.request.healthcare.StaticRagRequest
import com.example.zdravlje.data.model.request.login.TicketRequest
import com.example.zdravlje.data.model.request.notification.NotificationReadRequest
import com.example.zdravlje.data.model.request.notification.NotificationSearchRequest
import com.example.zdravlje.data.model.request.notification.NotificationSettingsRequest
import com.example.zdravlje.data.model.request.profile.SetPermission
import com.example.zdravlje.data.model.request.profile.SharedProfile
import com.example.zdravlje.data.model.response.calendar.FamilyDoctor
import com.example.zdravlje.data.model.response.calendar.FamilyTimeSlotsResponse
import com.example.zdravlje.data.model.response.calendar.MedicineResponse
import com.example.zdravlje.data.model.response.calendar.ScheduleResponse
import com.example.zdravlje.data.model.response.calendar.TimeslotRequest
import com.example.zdravlje.data.model.response.calendar.TimeslotResponse
import com.example.zdravlje.data.model.response.chat.GenerateCallResponse
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.model.response.chat.SearchPermisionResponse
import com.example.zdravlje.data.model.response.dashboard.future.DailyEvents
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.healthcare.AccessMedicalCartonResponse
import com.example.zdravlje.data.model.response.healthcare.AppointmentResponse
import com.example.zdravlje.data.model.response.healthcare.CategoryQAResponse
import com.example.zdravlje.data.model.response.healthcare.EventHistoryDataResponse
import com.example.zdravlje.data.model.response.healthcare.Institution
import com.example.zdravlje.data.model.response.healthcare.InstitutionsArrayResponse
import com.example.zdravlje.data.model.response.healthcare.InstitutionsResponse
import com.example.zdravlje.data.model.response.healthcare.InteractionHistoryResponse
import com.example.zdravlje.data.model.response.healthcare.InteractionMedicineResponse
import com.example.zdravlje.data.model.response.healthcare.MedicalCartonResponse
import com.example.zdravlje.data.model.response.healthcare.MedicamenteResponse
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.model.response.healthcare.QAResponse
import com.example.zdravlje.data.model.response.healthcare.RecommendationResponse
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.example.zdravlje.data.model.response.healthcare.SocialGroupCategoryResponse
import com.example.zdravlje.data.model.response.healthcare.StaticRagResponse
import com.example.zdravlje.data.model.response.healthcare.TopicQAResponse
import com.example.zdravlje.data.model.response.login.EidTokenResponse
import com.example.zdravlje.data.model.response.login.TicketResponse
import com.example.zdravlje.data.model.response.notification.Notification
import com.example.zdravlje.data.model.response.notification.NotificationResponse
import com.example.zdravlje.data.model.response.notification.NotificationSettingsResponse
import com.example.zdravlje.data.model.response.profile.AccountSynchronizeResponse
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AppApi {

    //Login
    @POST("/site/login-data-check")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("ticket/create-ticket-unauthorized-user")
    suspend fun createTicket(
        @Part("g-recaptcha-response") grecaptcha: RequestBody,
        @Part("queue") queue: RequestBody,
        @Part("title") title: RequestBody,
        @Part("submitter_email") submitterEmail: RequestBody,
        @Part("description") description: RequestBody,
        @Part("timezone") timezone: RequestBody,
        @Part attachment: MultipartBody.Part? = null
    ): Response<TicketResponse>

    @POST("/user/user-check-pin")
    suspend fun checkPin(@Body pinRequest: PinRequest): Response<Any>

    @POST("/site/qr-token")
    suspend fun generate6MonthToken(): Response<String>

    @POST("/site/login-anonymous")
    suspend fun anonimusLogin(@Body anonimusLoginRequest: AnonimusLoginRequest): Response<String>

    @POST("/site/rfzo-auth")
    suspend fun rfzoLogin(@Body rfzoAuthRequest: RfzoAuthRequest): Response<EidTokenResponse>

    //Dashboard
    @POST("/calendar/get-events-by-range")
    suspend fun getFutureDashboardEvents(@Body eventsAndActivitiesRequest: EventsAndActivitiesRequest): Response<List<DailyEvents>>

    @POST("/index.php/site/e-id-authenticate")
    suspend fun getEidToken(@Body eidRequest: EidTokenRequest): Response<EidTokenResponse>

//    @POST("/calendar/get-future-events-by-range")
//    suspend fun getFutureDashboardEventsA1(@Body eventsAndActivitiesRequest: EventsAndActivitiesRequest): Response<List<DailyEvents>>

    //Dashboard
    @POST("/calendar/get-events-by-range")
    suspend fun getPastDashboardEvents(@Body eventsAndActivitiesRequest: EventsAndActivitiesRequest): Response<List<DailyEvents>>

    @POST("/vc-support/check-token")
    suspend fun checkToken(@Body deviceInfo: UserDeviceInfo): Response<Any>

    //Profile
    @GET("/user/profile-view")
    suspend fun getUserInformation(): Response<UserInfo>

    @POST("/site/account-synchronize-status")
    suspend fun getSynchronizeStatus(@Body pinRequest: PinRequest): Response<List<AccountSynchronizeResponse>>

    @POST("/site/account-synchronize")
    suspend fun startSynchronizeStatus(@Body pinRequest: PinRequest): Response<Any>

    @POST("/user/update-my-profile")
    suspend fun updateMyProfile(@Body updateMyProfileRequest: UpdateMyProfileRequest): Response<Any>

    @POST("/patient/full-delete")
    suspend fun fullDeletePatient(): Response<Any>

    @Multipart
    @POST("/user/create-avatar")
    suspend fun updateAvatarProfile(
        @Part("pin") pin: RequestBody,
        @Part avatar: MultipartBody.Part
    ): Response<Any>

    //Chat
    @POST("/social-group-contact/search-contacts")
    suspend fun searchContacts(@Body searchModel: SearchContact): Response<List<Participant>>

    @POST("/social-group-contact/send-contact-request")
    suspend fun sendContactRequest(@Body sendContactRequest: SendContactRequest): Response<String>

     @POST("/social-group-contact/send-contact-request")
    suspend fun sendContactListRequest(@Body sendContactListRequest: SendContactListRequest): Response<String>

    @POST("/social-group-contact/search-permission")
    suspend fun searchPermission(@Body searchPermisionRequest: SearchPermisionRequest): Response<SearchPermisionResponse>

    @POST("social-group-contact/set-permission")
    suspend fun setPermission(@Body setPermission: SetPermission): Response<Any>

    @POST("social-group-contact/login-as-contact")
    suspend fun loginAsContact(@Body sharedProfile: SharedProfile): Response<LoginResponse>

    @POST("/social-group-contact/remove-contact")
    suspend fun removeContact(@Body removeContactRequest: RemoveContactRequest): Response<Any>

    @POST("/social-group-contact/cancel-friend-request")
    suspend fun cancelFriendRequest(@Body removeContactRequest: RemoveContactRequest): Response<String>

    @POST("/social-group-contact/deny-contact")
    suspend fun denyContactRequest(@Body removeContactRequest: RemoveContactRequest): Response<String>

    @POST("/social-group-contact/accept-contact")
    suspend fun acceptContactRequest(@Body acceptContactRequest: AcceptContactRequest): Response<String>

    //Calendar
    @POST("/patient-medicine/search-patient-medicine-group-by-medicine")
    suspend fun searchPatientMedicine(@Body recipeRequest: RecipeRequest): Response<List<PrescribedListMedicamentResponse>>

    @POST("/calendar/get-events-by-range")
    suspend fun getCalendarEventsForMonth(@Body eventsAndActivitiesRequest: EventsAndActivitiesRequest): Response<List<DailyEvents>>

    @POST("/e-uput/get-family-doctors")
    suspend fun getFamilyDoctors(@Body pinRequest: PinRequest): Response<List<FamilyDoctor>>

    @POST("/e-uput/get-family-doctor-time-slots")
    suspend fun getFamilyTimeSlots(@Body familyTimeSlots: FamilyTimeSlots): Response<List<ScheduleResponse>>

    @POST("/e-uput/create-exam")
    suspend fun scheduleAppointment(@Body timeslotRequest: TimeslotRequest): Response<TimeslotResponse>

    @POST("/e-uput/available-timeslots-for-telemedicine")
    suspend fun getFamilyTimeSlotsForTelemedicine(@Body familyTimeSlotsForTelemedicicne: FamilyTimeSlotsForTelemedicine): Response<List<FamilyTimeSlotsResponse>>

    @POST("/codebook/search-medicine-detail-alims-rfzo")
    suspend fun getMedicament(@Body medicineRequest: MedicineRequest): Response<List<MedicineResponse>>

    @POST("/calendar/event-realization")
    suspend fun realizeEvent(@Body realizeEventRequest: RealizeEventRequest): Response<Any>

    @POST("/calendar/create-events-and-notifications")
    suspend fun createEventAndNotification(@Body addEvent: AddEvent): Response<Any>

    @POST("/calendar/update-events-and-notifications")
    suspend fun updateEventAndNotification(@Body addEvent: AddEvent): Response<Any>

    @POST("/calendar/delete-event")
    suspend fun deleteEvent(@Body deleteEvent: DeleteEvent): Response<Any>

    @POST("/calendar/update-event")
    suspend fun updateEvent(@Body updateEvent: Event): Response<Any>

    @POST("/calendar/create-call")
    suspend fun createCall(@Body eventDetailsRequest: EventDetailsRequest): Response<GenerateCallResponse>

    @POST("/calendar/update-call")
    suspend fun updateCall(@Body eventDetailsRequest: EventDetailsRequest): Response<GenerateCallResponse>

    @POST("/skrining/available-screening-programs")
    suspend fun availableScreeningPrograms(@Body pinRequest: PinRequest): Response<ScreeningProgramsRequest>

    //eRecept - ordinaryList
    @POST("/e-recept/search-prescriptions")
    suspend fun searchPrescriptions(@Body recipeRequest: RecipeRequest): Response<List<PrescribedListMedicamentResponse>>

    @POST("/patient-medicine/delete")
    suspend fun deleteManualMedicamente(@Body deleteMedicamenteRequest: DeleteMedicamenteRequest): Response<Any>

    @POST("/patient-medicine/update")
    suspend fun updateMedicamente(@Body patmedDataRequest: PatmedDataRequest): Response<Any>

    //patient-medicine - ordinaryList
    @POST("/patient-medicine/add")
    suspend fun addMedicamentPrescribed(@Body addMedicamentRequest: AddMedicamentRequest): Response<Any>

    //eSickLeave
    @POST("/e-bolovanje/get-patient-sick-leaves")
    suspend fun getPatientSickLeaves(@Body eSickRequest: ESickRequest): Response<List<SickLeaveResponse>>

    @POST("/e-bolovanje/download-temp-inability-receipt")
    suspend fun getInabilityReceipt(@Body eSickRequestDTO: ESickRequestDTO): Response<ResponseBody>

    @POST("/e-bolovanje/download-temp-inability-report")
    suspend fun getInabilityReport(@Body eSickRequestDTO: ESickRequestDTO): Response<ResponseBody>

    @POST("/e-bolovanje/download-sick-leave-report")
    suspend fun getSickLeaveReport(@Body eSickRequestDTO: ESickRequestDTO): Response<ResponseBody>

    @POST("/e-bolovanje/download-commission-decision")
    suspend fun getDownloadCommisionDecission(@Body eSickRequestDTO: ESickRequestDTO): Response<ResponseBody>

    //MedicalCarton
    @POST("/e-karton/get-cases-data")
    suspend fun getCasesData(@Body medicalCartonRequest: MedicalCartonRequest): Response<List<MedicalCartonResponse>>

    @POST("/e-karton/download-vuz-report")
    suspend fun downloadReportMedicalCard(@Body medicalCartonPdfRequest: MedicalCartonPdfRequest): Response<ResponseBody>

    @POST("/e-karton/institution-list")
    suspend fun getListInstitutions(@Body medicalCartonRequest: MedicalCartonRequest): Response<InstitutionsResponse>

    @POST("/e-karton/log-case-data")
    suspend fun getAccessMedicalCarton(@Body medicalCartonRequest: MedicalCartonRequest): Response<List<AccessMedicalCartonResponse>>

    @POST("/e-karton/institutions")
    suspend fun getInstitutions(@Body medicalCartonRequest: MedicalCartonRequest): Response<InstitutionsArrayResponse>

    @POST("/e-karton/refresh-institution-data")
    suspend fun refreshInstitutionData(@Body medicalCartonRequest: MedicalCartonRequest): Response<String>

    //Prevention
    @POST("/social-group/search-social-group")
    suspend fun getCommonsThemes(@Body commonsThemesRequest: CommonsThemesRequest): Response<List<SocialGroupCategoryResponse>>

    @POST("/social-group/like-social-group-message")
    suspend fun getLikes(@Body commonsThemesRequest: CommonsThemesRequest): Response<List<SocialGroupCategoryResponse>>

    @POST("/social-group/follow-social-group")
    suspend fun followSocialGroup(@Body commonsThemesRequest: CommonsThemesRequest): Response<String>

    @POST("/citizen-engagement-institution/search")
    suspend fun getHealthInstitutionQA(@Body pin: String): Response<List<Institution>>

    @POST("/citizen-engagement-category/search")
    suspend fun getCategoryQA(@Body pin: String): Response<List<CategoryQAResponse>>

    @POST("/citizen-engagement-topic/search")
    suspend fun getTopicQA(@Body pin: String): Response<List<TopicQAResponse>>

    @POST("/interaction/get-all-medicines")
    suspend fun getAllMedicines(): Response<List<InteractionMedicineResponse>>

    @GET("/interaction/get-all-history")
    suspend fun getAllHistory(@Query("page") page: Int, @Query("size") size: Int): Response<InteractionHistoryResponse>

    @POST("/interaction/static-rag")
    suspend fun getStaticRag(@Query("patient_external_id") patientId: Int, @Body request: StaticRagRequest): Response<StaticRagResponse>

    @POST("/interaction/save-feedback")
    suspend fun saveFeedback(@Query("patient_external_id") patientId: Int, @Body feedbackRequest: FeedbackRequest): Response<JsonObject>

//    @POST("/citizen-engagement-patient/search")
//    suspend fun listAllQA(@Body pin: String): Response<List<QAResponse>>

    @POST("/citizen-engagement-patient/self-search")
    suspend fun listAllQA(@Body pin: String): Response<List<QAResponse>>

    @POST("/social-group-message/recommendations")
    suspend fun getRecommendations(@Body preventionDTO: PreventionDTO): Response<List<RecommendationResponse>>

    @POST("/codebook/search-by-letter")
    suspend fun getMedicaments(@Body preventionDTO: PreventionDTO): Response<List<MedicamenteResponse>>

    @POST("/codebook/search-icd10-group")
    suspend fun getMKB10(@Body pinRequest: PinRequest): Response<List<MKB10Response>>

    @POST("/codebook/search-icd10-subgroup")
    suspend fun getSubMKB10(@Body preventionDTO: PreventionDTO): Response<List<MKB10SubResponse>>

    @POST("/codebook/search-icd10")
    suspend fun getSubMKB10Groups(@Body preventionDTO: PreventionDTO): Response<List<MKB10SubResponseGroups>>

    //SchedulingAppointment
    @POST("/e-uput/get-future-exams")
    suspend fun getFutureExams(@Body pinRequest: PinRequest): Response<List<AppointmentResponse>>

    @POST("/e-uput/cancel-exam")
    suspend fun deleteExam(@Body examDeleteRequest: ExamDeleteRequest): Response<Any>

    @POST("/calendar/get-events-by-range")
    suspend fun getPastScheduledAppointments(@Body eventHistoryDataRequest: EventHistoryDataRequest): Response<List<EventHistoryDataResponse>>

    //Notifications
    @POST("/notification/search-notifications")
    suspend fun getNotifications(@Body notificationSearchRequest: NotificationSearchRequest): Response<List<NotificationResponse>>

    @POST("/notification/set-notifications-as-read")
    suspend fun setNotificationRead(@Body notificationReadRequest: NotificationReadRequest): Response<String>

    @POST("/notification/notification-count")
    suspend fun notificationCount(@Body notificationReadRequest: NotificationReadRequest): Response<Notification>

    @POST("/notification/get-notification-configuration")
    suspend fun getNotificationConf(@Body pin: PinRequest): Response<Notification>

    @POST("/notification/set-notification-configuration")
    suspend fun setNotificationConf(@Body notification: Notification): Response<Notification>

    @POST("/notification/set-notification-channel")
    suspend fun setNotificationSettings(@Body notificationSettingsRequest: NotificationSettingsRequest): Response<Any>

    @POST("/notification/get-notification-channel")
    suspend fun getNotificationSettings(@Body pin: PinRequest): Response<NotificationSettingsResponse>

    @POST("/site/testfirebase")
    suspend fun startNotification(@Body firebaseModel: FirebaseModel): Response<Any>
}