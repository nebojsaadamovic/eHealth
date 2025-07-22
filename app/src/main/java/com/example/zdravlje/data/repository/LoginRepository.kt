package com.example.zdravlje.data.repository

import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.AnonimusLoginRequest
import com.example.zdravlje.data.model.request.EidTokenRequest
import com.example.zdravlje.data.model.request.FirebaseModel
import com.example.zdravlje.data.model.request.LoginRequest
import com.example.zdravlje.data.model.request.LoginResponse
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.RfzoAuthRequest
import com.example.zdravlje.data.model.request.UserDeviceInfo
import com.example.zdravlje.data.model.request.login.TicketRequest
import com.example.zdravlje.data.model.response.login.AnonimusLoginResponse
import com.example.zdravlje.data.model.response.login.EidTokenResponse
import com.example.zdravlje.data.model.response.login.TicketResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class LoginRepository {

    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return RetrofitInstance.appApi.login(loginRequest)
    }

    suspend fun createTicket(
        ticketRequest: TicketRequest,
        filePart: MultipartBody.Part?
    ): Response<TicketResponse> {
        val recaptcha = (ticketRequest.grecaptcharesponse ?: "")
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val queue = (ticketRequest.queue ?: "")
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val title = (ticketRequest.title ?: "")
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val submitterEmail = (ticketRequest.submitterEmail ?: "")
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val description = (ticketRequest.description ?: "")
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val timezone = (ticketRequest.timezone ?: "")
            .toRequestBody("text/plain".toMediaTypeOrNull())

        return RetrofitInstance.appApi.createTicket(
            grecaptcha = recaptcha,
            queue = queue,
            title = title,
            submitterEmail = submitterEmail,
            description = description,
            timezone = timezone,
            attachment = filePart
        )
    }

    suspend fun loginRfzo(loginRequest: RfzoAuthRequest): Response<EidTokenResponse> {
        return RetrofitInstance.appApi.rfzoLogin(loginRequest)
    }

    suspend fun anonimusLogin(anonimusLoginRequest: AnonimusLoginRequest): Response<String> {
        return RetrofitInstance.appApi.anonimusLogin(anonimusLoginRequest)
    }

    suspend fun getEidToken(eidRequest: EidTokenRequest): Response<EidTokenResponse> {
        return RetrofitInstance.appApi.getEidToken(eidRequest)
    }

    suspend fun generate6MonthToken(): Response<String> {
        return RetrofitInstance.appApi.generate6MonthToken()
    }

    suspend fun checkToken( deviceInfo: UserDeviceInfo): Response<Any> {
        return RetrofitInstance.appApi.checkToken(deviceInfo)
    }

    suspend fun checkPin( pinRequest: PinRequest): Response<Any> {
        return RetrofitInstance.appApi.checkPin(pinRequest)
    }

    suspend fun startFirebase( firebaseModel: FirebaseModel): Response<Any> {
        return RetrofitInstance.appApi.startNotification(firebaseModel)
    }
}