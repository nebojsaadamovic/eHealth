package com.example.zdravlje.data.repository.healthcare


import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.calendar.ExamDeleteRequest
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
import com.example.zdravlje.data.model.response.healthcare.AccessMedicalCartonResponse
import com.example.zdravlje.data.model.response.healthcare.AppointmentResponse
import com.example.zdravlje.data.model.response.healthcare.CategoryQAResponse
import com.example.zdravlje.data.model.response.healthcare.EventHistoryDataResponse
import com.example.zdravlje.data.model.response.healthcare.Institution
import com.example.zdravlje.data.model.response.healthcare.InstitutionsArrayResponse
import com.example.zdravlje.data.model.response.healthcare.InstitutionsResponse
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
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Query

class HealthcareRepository {

    suspend fun searchPrescriptions(recipeRequest: RecipeRequest): Response<List<PrescribedListMedicamentResponse>> {
        return RetrofitInstance.appApi.searchPrescriptions(recipeRequest)
    }

    suspend fun deleteManualMedicamente(deleteMedicamenteRequest: DeleteMedicamenteRequest): Response<Any> {
        return RetrofitInstance.appApi.deleteManualMedicamente(deleteMedicamenteRequest)
    }

    suspend fun updateMedicamente(patmedDataRequest: PatmedDataRequest): Response<Any> {
        return RetrofitInstance.appApi.updateMedicamente(patmedDataRequest)
    }

    suspend fun getPatientSickLeaves(eSickRequest: ESickRequest): Response<List<SickLeaveResponse>> {
        return RetrofitInstance.appApi.getPatientSickLeaves(eSickRequest)
    }

    suspend fun getInabilityReceipt(eSickRequestDTO: ESickRequestDTO): Response<ResponseBody> {
        return RetrofitInstance.appApi.getInabilityReceipt(eSickRequestDTO)
    }

    suspend fun getInabilityReport(eSickRequestDTO: ESickRequestDTO): Response<ResponseBody> {
        return RetrofitInstance.appApi.getInabilityReport(eSickRequestDTO)
    }

    suspend fun getSickLeaveReport(eSickRequestDTO: ESickRequestDTO): Response<ResponseBody> {
        return RetrofitInstance.appApi.getSickLeaveReport(eSickRequestDTO)
    }

    suspend fun getDownloadCommisionDecission(eSickRequestDTO: ESickRequestDTO): Response<ResponseBody> {
        return RetrofitInstance.appApi.getDownloadCommisionDecission(eSickRequestDTO)
    }

    suspend fun getCasesData(medicalCardRequest: MedicalCartonRequest): Response<List<MedicalCartonResponse>> {
        return RetrofitInstance.appApi.getCasesData(medicalCardRequest)
    }

    suspend fun downloadReportMedicalCard(medicalCardPdfRequest: MedicalCartonPdfRequest): Response<ResponseBody> {
        return RetrofitInstance.appApi.downloadReportMedicalCard(medicalCardPdfRequest)
    }

    suspend fun getListInstitutions(medicalCartonRequest: MedicalCartonRequest): Response<InstitutionsResponse> {
        return RetrofitInstance.appApi.getListInstitutions(medicalCartonRequest)
    }

    suspend fun getAccessMedicalCarton(medicalCartonRequest: MedicalCartonRequest): Response<List<AccessMedicalCartonResponse>> {
        return RetrofitInstance.appApi.getAccessMedicalCarton(medicalCartonRequest)
    }

    suspend fun getInstitutions(medicalCartonRequest: MedicalCartonRequest): Response<InstitutionsArrayResponse> {
        return RetrofitInstance.appApi.getInstitutions(medicalCartonRequest)
    }

    suspend fun refreshInstitutionData(medicalCartonRequest: MedicalCartonRequest): Response<String> {
        return RetrofitInstance.appApi.refreshInstitutionData(medicalCartonRequest)
    }

    suspend fun getCommonsThemes(commonsThemesRequest: CommonsThemesRequest): Response<List<SocialGroupCategoryResponse>> {
        return RetrofitInstance.appApi.getCommonsThemes(commonsThemesRequest)
    }

    suspend fun getLikes(commonsThemesRequest: CommonsThemesRequest): Response<List<SocialGroupCategoryResponse>> {
        return RetrofitInstance.appApi.getLikes(commonsThemesRequest)
    }

    suspend fun followSocialGroup(commonsThemesRequest: CommonsThemesRequest): Response<String> {
        return RetrofitInstance.appApi.followSocialGroup(commonsThemesRequest)
    }

    suspend fun getHealthInstitutionQA(pin: String): Response<List<Institution>> {
        return RetrofitInstance.appApi.getHealthInstitutionQA(pin)
    }

    suspend fun getCategoryQA(pin: String): Response<List<CategoryQAResponse>> {
        return RetrofitInstance.appApi.getCategoryQA(pin)
    }

    suspend fun getTopicQA(pin: String): Response<List<TopicQAResponse>> {
        return RetrofitInstance.appApi.getTopicQA(pin)
    }

    suspend fun listAllQA(pin: String): Response<List<QAResponse>> {
        return RetrofitInstance.appApi.listAllQA(pin)
    }

    suspend fun getRecommendations(prventionDto: PreventionDTO): Response<List<RecommendationResponse>> {
        return RetrofitInstance.appApi.getRecommendations(prventionDto)
    }

    suspend fun getMedicaments(prventionDto: PreventionDTO): Response<List<MedicamenteResponse>> {
        return RetrofitInstance.appApi.getMedicaments(prventionDto)
    }

    suspend fun getMKB10(pin: PinRequest): Response<List<MKB10Response>> {
        return RetrofitInstance.appApi.getMKB10(pin)
    }

    suspend fun getSubMKB10(preventionDTO: PreventionDTO): Response<List<MKB10SubResponse>> {
        return RetrofitInstance.appApi.getSubMKB10(preventionDTO)
    }

    suspend fun getSubMKB10Groups(preventionDTO: PreventionDTO): Response<List<MKB10SubResponseGroups>> {
        return RetrofitInstance.appApi.getSubMKB10Groups(preventionDTO)
    }

    suspend fun addMedicamentPrescribed(addMedicamentRequest: AddMedicamentRequest): Response<Any> {
        return RetrofitInstance.appApi.addMedicamentPrescribed(addMedicamentRequest)
    }

    suspend fun getFutureExams(pinRequest: PinRequest): Response<List<AppointmentResponse>> {
        return RetrofitInstance.appApi.getFutureExams(pinRequest)
    }

    suspend fun getPastScheduledAppointments(eventHistoryDataRequest: EventHistoryDataRequest): Response<List<EventHistoryDataResponse>> {
        return RetrofitInstance.appApi.getPastScheduledAppointments(eventHistoryDataRequest)
    }

    suspend fun deleteExam(examDeleteRequest: ExamDeleteRequest): Response<Any> {
        return RetrofitInstance.appApi.deleteExam(examDeleteRequest)
    }

    suspend fun getAllMedicines(): Response<List<InteractionMedicineResponse>> {
        return RetrofitInstance.appApi.getAllMedicines()
    }

    suspend fun getStaticRag(
        patientId: Int,
        request: StaticRagRequest
    ): Response<StaticRagResponse> {
        return RetrofitInstance.appApi.getStaticRag(patientId, request)
    }

    suspend fun saveFeedback(
        patientId: Int,
        feedbackRequest: FeedbackRequest
    ): Response<JsonObject> {
        return RetrofitInstance.appApi.saveFeedback(patientId, feedbackRequest)
    }

}